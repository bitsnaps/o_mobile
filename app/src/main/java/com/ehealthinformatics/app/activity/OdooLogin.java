package com.ehealthinformatics.app.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ehealthinformatics.App;
import com.ehealthinformatics.R;
import com.ehealthinformatics.app.utils.LoadingUtils;
import com.ehealthinformatics.data.dto.SyncConfig;
import com.ehealthinformatics.core.auth.OdooAccountManager;
import com.ehealthinformatics.core.auth.OdooAuthenticator;
import com.ehealthinformatics.core.rpc.Odoo;
import com.ehealthinformatics.core.rpc.handler.OdooVersionException;
import com.ehealthinformatics.core.rpc.listeners.IDatabaseListListener;
import com.ehealthinformatics.core.rpc.listeners.IOdooConnectionListener;
import com.ehealthinformatics.core.rpc.listeners.IOdooLoginCallback;
import com.ehealthinformatics.core.rpc.listeners.OdooError;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.core.support.OdooUserLoginSelectorDialog;
import com.ehealthinformatics.core.utils.IntentUtils;
import com.ehealthinformatics.core.utils.OResource;
import com.ehealthinformatics.config.OConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OdooLogin extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener, OdooUserLoginSelectorDialog.IUserLoginSelectListener,
        IOdooConnectionListener, IOdooLoginCallback, AdapterView.OnItemSelectedListener {

    public static final String TAG = OdooActivity.class.getSimpleName();

    private EditText edtUsername, edtPassword, edtSelfHosted;
    private Boolean mCreateAccountRequest = false;
    private Boolean mSelfHostedURL = false;
    private Boolean mConnectedToServer = false;
    private Boolean mAutoLogin = false;
    private Boolean mRequestedForAccount = false;
    private AccountCreator accountCreator = null;
    private Spinner serverSpinner = null;
    private Spinner databaseSpinner = null;
    private List<String> databases = new ArrayList<>();
    private TextView mLoginProcessStatus = null;
    private App mApp;
    private Odoo mOdoo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
        mApp = (App) getApplicationContext();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(OdooAuthenticator.KEY_NEW_ACCOUNT_REQUEST))
                mCreateAccountRequest = true;
            if (extras.containsKey(OdooActivity.KEY_ACCOUNT_REQUEST)) {
                mRequestedForAccount = true;
                setResult(RESULT_CANCELED);
            }
        }
        if (!mCreateAccountRequest) {
            if (OdooAccountManager.anyActiveUser(this)) {
                //TODO:Put i preferecnces
                startSplashActivity();
                return;
            } else if (OdooAccountManager.hasAnyAccount(this)) {
                onRequestAccountSelect();
            }
        }
        init();
    }

    private void init() {
        mLoginProcessStatus = findViewById(R.id.login_process_status);
        TextView mTermsCondition = findViewById(R.id.termsCondition);
        mTermsCondition.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.forgot_password).setOnClickListener(this);
        findViewById(R.id.create_account).setOnClickListener(this);
        findViewById(R.id.txvAddSelfHosted).setOnClickListener(this);
        edtSelfHosted = findViewById(R.id.edtSelfHostedURL);
        edtSelfHosted.setText(OConstants.URL_ODOO);
        serverSpinner = findViewById(R.id.spinnerServerList);
        serverSpinner.setOnItemSelectedListener(this);
        initServers();
    }

    private void startSplashActivity() {
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txvAddSelfHosted:
                toggleSelfHostedURL();
                break;
            case R.id.btnLogin:
                loginUser();
                break;
            case R.id.forgot_password:
                IntentUtils.openURLInBrowser(this, OConstants.URL_ODOO_RESET_PASSWORD);
                break;
            case R.id.create_account:
                IntentUtils.openURLInBrowser(this, OConstants.URL_ODOO_SIGN_UP);
                break;
        }
    }

    private void toggleSelfHostedURL() {
        TextView txvAddSelfHosted = (TextView) findViewById(R.id.txvAddSelfHosted);
        if (!mSelfHostedURL) {
            mSelfHostedURL = true;
            findViewById(R.id.layoutSelfHosted).setVisibility(View.VISIBLE);
            edtSelfHosted.setOnFocusChangeListener(this);
            edtSelfHosted.requestFocus();
            txvAddSelfHosted.setText(R.string.label_login_with_odoo);
        } else {
            findViewById(R.id.layoutBorderDB).setVisibility(View.GONE);
            findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
            findViewById(R.id.layoutSelfHosted).setVisibility(View.GONE);
            mSelfHostedURL = false;
            txvAddSelfHosted.setText(R.string.label_add_self_hosted_url);
            edtSelfHosted.setText("");
        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerServerList){
            edtSelfHosted.setText(OConstants.URL_ODOO.split(",")[position]);
            onFocusChange(edtSelfHosted, false);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (v.getId() == R.id.edtSelfHostedURL && !hasFocus) {
                    if (!TextUtils.isEmpty(edtSelfHosted.getText())
                            && validateURL(edtSelfHosted.getText().toString())) {
                        edtSelfHosted.setError(null);
                        if (mAutoLogin) {
                            findViewById(R.id.controls).setVisibility(View.GONE);
                            findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
                            mLoginProcessStatus.setText(OResource.string(OdooLogin.this,
                                    R.string.status_connecting_to_server));
                        }
                        findViewById(R.id.imgValidURL).setVisibility(View.GONE);
                        findViewById(R.id.serverURLCheckProgress).setVisibility(View.VISIBLE);
                        findViewById(R.id.layoutBorderDB).setVisibility(View.GONE);
                        findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
                        String test_url = createServerURL(edtSelfHosted.getText().toString());
                        Log.v("", "Testing URL :" + test_url);
                        try {
                            Odoo.createInstance(OdooLogin.this, test_url).setOnConnect(OdooLogin.this);
                        } catch (OdooVersionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 500);
    }

    private boolean validateURL(String url) {
        return (url.contains("."));
    }

    private String createServerURL(String server_url) {
        StringBuilder serverURL = new StringBuilder();
        if (!server_url.contains("http://") && !server_url.contains("https://")) {
            serverURL.append("http://");
        }
        serverURL.append(server_url);
        return serverURL.toString();
    }

    // User Login
    private void loginUser() {
        Log.v("", "LoginUser()");
        String serverURL = createServerURL((mSelfHostedURL) ? edtSelfHosted.getText().toString() :
                OConstants.URL_ODOO);
        String databaseName;
        edtUsername = (EditText) findViewById(R.id.edtUserName);
        edtPassword = (EditText) findViewById(R.id.edtPassword);

        if (mSelfHostedURL) {
            edtSelfHosted.setError(null);
            if (TextUtils.isEmpty(edtSelfHosted.getText())) {
                edtSelfHosted.setError(OResource.string(this, R.string.error_provide_server_url));
                edtSelfHosted.requestFocus();
                return;
            }
            if (databaseSpinner != null && databases.size() > 1 && databaseSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, OResource.string(this, R.string.label_select_database), Toast.LENGTH_LONG).show();
                findViewById(R.id.controls).setVisibility(View.VISIBLE);
                findViewById(R.id.login_progress).setVisibility(View.GONE);
                return;
            }

        }
        edtUsername.setError(null);
        edtPassword.setError(null);
        if (TextUtils.isEmpty(edtUsername.getText())) {
            edtUsername.setError(OResource.string(this, R.string.error_provide_username));
            edtUsername.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError(OResource.string(this, R.string.error_provide_password));
            edtPassword.requestFocus();
            return;
        }
        findViewById(R.id.controls).setVisibility(View.GONE);
        findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this,
                R.string.status_connecting_to_server));
        if (mConnectedToServer) {
            databaseName = databases.get(0);
            if (databaseSpinner != null) {
                databaseName = databases.get(databaseSpinner.getSelectedItemPosition());
            }
            mAutoLogin = false;
            loginProcess(databaseName);
        } else {
            mAutoLogin = true;
            try {
                Odoo.createInstance(OdooLogin.this, serverURL).setOnConnect(OdooLogin.this);
            } catch (OdooVersionException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatabases() {
        if (databases.size() > 1) {
            findViewById(R.id.layoutBorderDB).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutDatabase).setVisibility(View.VISIBLE);
            databaseSpinner = (Spinner) findViewById(R.id.spinnerDatabaseList);
            databases.add(0, OResource.string(this, R.string.label_select_database));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    databases);
            databaseSpinner.setAdapter(adapter);
        } else {
            databaseSpinner = null;
            findViewById(R.id.layoutBorderDB).setVisibility(View.GONE);
            findViewById(R.id.layoutDatabase).setVisibility(View.GONE);
        }
    }

    private void initServers(){
        ArrayAdapter<String> servereAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                Arrays.asList(OConstants.URL_ODOO.split(",")));
        serverSpinner.setAdapter(servereAdapter);
    }

    @Override
    public void onUserSelected(OUser user) {
        OdooAccountManager.login(this, user.getAndroidName());
        startSplashActivity();
    }

    @Override
    public void onRequestAccountSelect() {
        OdooUserLoginSelectorDialog dialog = new OdooUserLoginSelectorDialog(this);
        dialog.setUserLoginSelectListener(this);
        dialog.show();
    }

    @Override
    public void onNewAccountRequest() {
        init();
    }


    @Override
    public void onConnect(Odoo odoo) {
        Log.v("Odoo", "Connected to server.");
        mOdoo = odoo;
        databases.clear();
        findViewById(R.id.serverURLCheckProgress).setVisibility(View.GONE);
        edtSelfHosted.setError(null);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_connected_to_server));
        mOdoo.getDatabaseList(new IDatabaseListListener() {
            @Override
            public void onDatabasesLoad(List<String> strings) {
                databases.addAll(strings);
                showDatabases();
                mConnectedToServer = true;
                findViewById(R.id.imgValidURL).setVisibility(View.VISIBLE);
                if (mAutoLogin) {
                    loginUser();
                }
            }
        });
    }

    @Override
    public void onError(OdooError error) {
        // Some error occurred
        if (error.getResponseCode() == Odoo.ErrorCode.InvalidURL.get() ||
                error.getResponseCode() == -1) {
            findViewById(R.id.controls).setVisibility(View.VISIBLE);
            findViewById(R.id.login_progress).setVisibility(View.GONE);
            edtSelfHosted.setError(OResource.string(OdooLogin.this, R.string.error_invalid_odoo_url));
            edtSelfHosted.requestFocus();
        }
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);
        findViewById(R.id.serverURLCheckProgress).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancelSelect() {
    }

    private void loginProcess(String database) {
        Log.v("", "LoginProcess");
        final String username = edtUsername.getText().toString();
        final String password = edtPassword.getText().toString();
        Log.v("", "Processing Self Hosted Server Login");
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_logging_in));
        mOdoo.authenticate(username, password, database, this);
    }

    @Override
    public void onLoginSuccess(Odoo odoo, OUser user) {
        mApp.setOdoo(odoo, user);
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_login_success));
        mOdoo = odoo;
        if (accountCreator != null) {
            accountCreator.cancel(true);
        }
        accountCreator = new AccountCreator();
        accountCreator.execute(user);
    }

    @Override
    public void onLoginFail(OdooError error) {
        loginFail(error);
    }

    private void loginFail(OdooError error) {
        Log.e("Login Failed", error.getMessage());
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);
        edtUsername.setError(OResource.string(this, R.string.error_invalid_username_or_password));
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private class AccountCreator extends AsyncTask<OUser, String, SyncConfig> {

        private OUser mUser;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoginProcessStatus.setText("Syncing User Configuration");
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mLoginProcessStatus.setText(values[0]);
        }

        @Override
        protected SyncConfig doInBackground(OUser... params) {
            mUser = params[0];
            LoadingUtils.ArtifactsLoader daos = new LoadingUtils.ArtifactsLoader(OdooLogin.this, mUser);
            try {
                if (OdooAccountManager.createAccount(OdooLogin.this, mUser)) {
                    mUser = OdooAccountManager.getDetails(OdooLogin.this, mUser.getAndroidName());
                    OdooAccountManager.login(OdooLogin.this, mUser.getAndroidName());
                    return daos.init();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
                removeUser(mUser);
            }
            return null;
        }

        @Override
        protected void onPostExecute(SyncConfig syncConfig) {
            super.onPostExecute(syncConfig);
            if (syncConfig == null || syncConfig.getUser() == null){
                mLoginProcessStatus.setText("Unable to complete syncing configuration with server");
                toast("Unable to complete syncing configuration with server");
                findViewById(R.id.controls).setVisibility(View.VISIBLE);
                findViewById(R.id.login_progress).setVisibility(View.GONE);
            } else if(syncConfig.getPosSession() == null){
                mLoginProcessStatus.setText("");
                toast("Unable to sync open session to Device");
                findViewById(R.id.controls).setVisibility(View.VISIBLE);
                findViewById(R.id.login_progress).setVisibility(View.GONE);
            } else {
                mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_redirecting));
                OConstants.CURRENCY_SYMBOL = syncConfig.getPosSession().getConfig().getPriceList().getCurrency().getSymbol();
                mUser.setPosSessionId(syncConfig.getPosSession().getServerId());
                mUser.setCurrencySymbol(OConstants.CURRENCY_SYMBOL);
                OUser user = OdooAccountManager.updateUserData(App.getContext(), mUser);
                mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_redirecting));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mRequestedForAccount)
                            startOdooActivity();
                        else {
                            Intent intent = new Intent();
                            intent.putExtra(OdooActivity.KEY_NEW_USER_NAME, mUser.getAndroidName());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }, 1500);
            }

        }
    }

    private void startOdooActivity() {
        startActivity(new Intent(this, OdooActivity.class));
        finish();
    }

    private void removeUser(OUser oUser){
        OdooAccountManager.logout(OdooLogin.this, oUser.getAndroidName());
        OdooAccountManager.removeAccount(OdooLogin.this, oUser.getAndroidName());
        OdooAccountManager.dropDatabase(oUser);
    }
}