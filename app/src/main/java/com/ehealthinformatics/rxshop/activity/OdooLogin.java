package com.ehealthinformatics.rxshop.activity;

import android.content.Intent;
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

import com.ehealthinformatics.RxShop;
import com.ehealthinformatics.odoorx.core.base.auth.IConfigLoadListener;
import com.ehealthinformatics.odoorx.core.base.auth.ISyncConfig;
import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.rxshop.utils.LoadingUtils;
import com.ehealthinformatics.odoorx.core.base.auth.ILoginProgressStatus;
import com.ehealthinformatics.odoorx.core.base.auth.OUserAccount;
import com.ehealthinformatics.odoorx.core.config.OConstants;
import com.ehealthinformatics.odoorx.core.base.auth.OdooAccountManager;
import com.ehealthinformatics.odoorx.core.base.auth.OdooAuthenticator;
import com.ehealthinformatics.odoorx.core.base.rpc.Odoo;
import com.ehealthinformatics.odoorx.core.base.rpc.handler.OdooVersionException;
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooUserLoginSelectorDialog;
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils;
import com.ehealthinformatics.odoorx.core.base.utils.OResource;

import java.util.Arrays;
import java.util.List;

public class OdooLogin extends AppCompatActivity implements View.OnClickListener,
        View.OnFocusChangeListener, OdooUserLoginSelectorDialog.IUserLoginSelectListener,
        ILoginProgressStatus, IConfigLoadListener,  AdapterView.OnItemSelectedListener {

    public static final String TAG = OdooActivity.class.getSimpleName();

    private EditText edtUsername, edtPassword, edtSelfHosted;
    private Boolean mCreateAccountRequest = false;
    private Boolean mSelfHostedURL = false;
    private Boolean mConnectedToServer = false;
    private Boolean mAutoLogin = false;
    private Boolean mRequestedForAccount = false;
    private Spinner serverSpinner = null;
    private Spinner databaseSpinner = null;
    private TextView mLoginProcessStatus = null;
    OUserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_login);
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
        edtUsername = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
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
                        String odooUrl = createServerURL(edtSelfHosted.getText().toString());
                        try {
                            userAccount = OUserAccount.getInstance(RxShop.getAppContext(), odooUrl, OdooLogin.this, OdooLogin.this);
                            Log.v("", "Testing URL :" + odooUrl);
                        } catch (OdooVersionException e) {
                            display("Error creating odoo client...");
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
        String databaseName = userAccount.getDatabases().get(0);

        if(validate()) {
            findViewById(R.id.controls).setVisibility(View.GONE);
            findViewById(R.id.login_progress).setVisibility(View.VISIBLE);
            mLoginProcessStatus.setText(OResource.string(OdooLogin.this,
                    R.string.status_connecting_to_server));
            final String username = edtUsername.getText().toString();
            final String password = edtPassword.getText().toString();
            if (mConnectedToServer) {
                if (databaseSpinner != null) {
                    databaseName = userAccount.getDatabases().get(databaseSpinner.getSelectedItemPosition());
                }
                mAutoLogin = false;
            } else {
                mAutoLogin = true;
            }
            userAccount.authenticate(username, password, databaseName);
        }

    }

    private boolean validate(){
        if (mSelfHostedURL) {
            edtSelfHosted.setError(null);
            if (TextUtils.isEmpty(edtSelfHosted.getText())) {
                edtSelfHosted.setError(OResource.string(this, R.string.error_provide_server_url));
                edtSelfHosted.requestFocus();
                return false;
            }
            if (databaseSpinner != null && userAccount.getDatabases().size() > 1 && databaseSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(this, OResource.string(this, R.string.label_select_database), Toast.LENGTH_LONG).show();
                findViewById(R.id.controls).setVisibility(View.VISIBLE);
                findViewById(R.id.login_progress).setVisibility(View.GONE);
                return false;
            }
        }
        edtUsername.setError(null);
        edtPassword.setError(null);
        if (TextUtils.isEmpty(edtUsername.getText())) {
            edtUsername.setError(OResource.string(this, R.string.error_provide_username));
            edtUsername.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edtPassword.getText())) {
            edtPassword.setError(OResource.string(this, R.string.error_provide_password));
            edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void showDatabases(List<String> databases) {
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
    public void onCancelSelect() {

    }

    @Override
    public void onConnectionError(OdooError error) {
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


        Log.e("Login Failed", error.getMessage());
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);
        edtUsername.setError(OResource.string(this, R.string.error_invalid_username_or_password));
    }

    private void loginProcess(String username, String password, String database) {
        Log.v("", "LoginProcess");
        Log.v("", "Processing Self Hosted Server Login");
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_logging_in));
    }

    @Override
    public void onConnect(List<String> databases) {
        Log.v("Odoo", "Connected to server.");
        findViewById(R.id.serverURLCheckProgress).setVisibility(View.GONE);
        edtSelfHosted.setError(null);
        display(OResource.string(OdooLogin.this, R.string.status_connected_to_server));
        showDatabases(databases);
        if (mAutoLogin) {
            loginUser();
        }
        mConnectedToServer = true;
    }

    @Override
    public void onLoginSuccess(final OUser oUser) {
        mLoginProcessStatus.setText(OResource.string(OdooLogin.this, R.string.status_login_success));
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startOdooActivity() {
        startActivity(new Intent(this, OdooActivity.class));
        finish();
    }

    @Override
    public ISyncConfig onStartConfigLoad() {
        display("Loading Config");
        RxShop.initDaos(userAccount);
        LoadingUtils.ArtifactsLoader artifactsLoader = new LoadingUtils.ArtifactsLoader(userAccount);
        return artifactsLoader.load();
    }

    @Override
    public void onConfigLoadError(OdooError e) {
        display(e.getMessage());
        findViewById(R.id.controls).setVisibility(View.VISIBLE);
        findViewById(R.id.login_progress).setVisibility(View.GONE);

    }

    @Override
    public void onConfigLoadSuccess(ISyncConfig syncConfig) {
        display("Loaded Config Successfully");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mRequestedForAccount)
                    startOdooActivity();
                else {
                    Intent intent = new Intent();
                    intent.putExtra(OdooActivity.KEY_NEW_USER_NAME, userAccount.getOUser().getAndroidName());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }, 1500);
    }

    private  void display(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoginProcessStatus.setText(message);
            }
        });
    }
}