package com.ehealthinformatics.rxshop.activity.shopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.ehealthinformatics.RxShop;
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener;
import com.ehealthinformatics.odoorx.core.base.rpc.helper.ODomain;
import com.ehealthinformatics.odoorx.core.base.utils.OAppBarUtils;
import com.ehealthinformatics.odoorx.core.data.dao.PosOrderLineDao;
import com.ehealthinformatics.odoorx.core.data.dao.QueryFields;
import com.ehealthinformatics.odoorx.core.data.viewmodel.PosOrderViewModel;
import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.odoorx.rxshop.data.adapter.CartListAdapter;
import com.ehealthinformatics.odoorx.core.data.dao.ProductDao;
import com.ehealthinformatics.odoorx.core.data.dto.PosOrder;
import com.ehealthinformatics.odoorx.core.data.dto.PosOrderLine;
import com.ehealthinformatics.odoorx.core.data.dto.Product;
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooCompatActivity;
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils;
import com.ehealthinformatics.odoorx.core.data.db.Columns;


public class PosOrderCart extends OdooCompatActivity implements OnItemClickListener<PosOrderLine> {

    private Integer posOrderId;
    private PosOrder posOrder;
    private CartListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private MenuItem miNew;
    private RelativeLayout rlLoading;
    private RelativeLayout rlNoItems;
    private LinearLayout llOrderCart;
    private TextView tvCheckout;
    private TextView tvTotalAmount;
    private boolean cartEditMode = false;
    private PosOrderViewModel model;
    private ProductDao productDao;
    private TextView noItemTitle;
    private TextView noItemMessage;
    private MaterialRippleLayout mrlCheckoutCommand;
    PosOrderLineDao posOrderLineDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart_simple);
        init();
    }

    private void init(){
        initIntent();
        initToolbar();
        initUI();
        initAdapter();
        initList();
        initListeners();
        initDataLoad();
        productDao = RxShop.getDao(ProductDao.class);
        posOrderLineDao = RxShop.getDao(PosOrderLineDao.class);
    }

    private void initIntent(){
        cartEditMode = getIntent().getBooleanExtra(IntentUtils.IntentParams.EDIT_MODE, false);
        posOrderId = getIntent().getIntExtra(IntentUtils.IntentParams.ID, 0);

    }

    private void initUI(){
        tvTotalAmount = findViewById(R.id.tv_cart_total_amount);
        tvCheckout = findViewById(R.id.tv_cart_checkout);
        rlLoading = findViewById(R.id.rl_loading);
        llOrderCart = findViewById(R.id.ll_order_cart);
        noItemTitle = findViewById(R.id.tv_empty_title);
        noItemMessage = findViewById(R.id.tv_empty_sub_title);
        mrlCheckoutCommand = findViewById(R.id.mrlNext);
        rlNoItems = findViewById(R.id.rl_empty_list);
        noItemTitle.setText("Cart Empty");
        noItemMessage.setText("Click on the search icon to add order line.");
    }

    private void initAdapter() {
        mAdapter = new CartListAdapter(PosOrderCart.this, cartEditMode);
    }

    private void initList() {
        recyclerView = findViewById(R.id.rv_cart_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void initListeners() {
        tvCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadActivity(posOrder);
            }
        });
    }

    private void initDataLoad(){
        model = ViewModelProviders.of(this).get(PosOrderViewModel.class);
        model.loadData(posOrderId);
        model.getSelected().observe(this, new Observer<PosOrder>() {
            @Override
            public void onChanged(@Nullable final PosOrder posOrder) {
                PosOrderCart.this.posOrder = posOrder;
                mAdapter.changeOrder(posOrder);
                updateUI();

            }
        });
    }

    private void initToolbar() {
        OAppBarUtils.setAppBar(this, R.id.tb_shopping_cart, true);
        getSupportActionBar().setTitle("Order");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        this.miNew = menu.findItem(R.id.action_add);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() ==  R.id.action_add) {
            Intent searchIntent = new Intent(this, SearchToolbarLight.class);
            startActivityForResult(searchIntent, 1);
        } else if (item.getItemId() ==  R.id.action_sync) {
            ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Syncing. Please wait...", true);
            quickSync(dialog);

        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(View view, PosOrderLine posOrderLine, int position) {
        updateUI();
    }

    private void updateUI() {
        posOrder.recalculate();
        tvTotalAmount.setText(String.format("₦ %.2f", posOrder.getAmountTotal()));
        if(posOrder.getLines().size() < 1){
            llOrderCart.setVisibility(View.GONE);
            rlNoItems.setVisibility(View.VISIBLE);
        } else {
            llOrderCart.setVisibility(View.VISIBLE);
            rlNoItems.setVisibility(View.GONE);
        }
        if(posOrder.getState().equals(Columns.PosOrder.State.PAID)){
            mrlCheckoutCommand.setBackgroundColor(ContextCompat.getColor(this, R.color.green_400));
            tvCheckout.setEnabled(false);
            tvCheckout.setText("PAID");
        }
            tvCheckout.setEnabled(!posOrder.getLines().isEmpty());

        if(!cartEditMode || !posOrder.getState().equals(Columns.PosOrder.State.DRAFT)) miNew.setVisible(false);
        showProgress(false);
    }

    public void showProgress(Boolean show){
        if(show) {
            llOrderCart.setVisibility(View.GONE);
            rlLoading.setVisibility(View.VISIBLE);
            }
        else {
            llOrderCart.setVisibility(View.VISIBLE);
            rlLoading.setVisibility(View.INVISIBLE);  }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    int productId = data.getIntExtra(Columns.PosOrderLine.product_id, 0);
                    if(productId > 0){
                        Product product = productDao.get(productId, QueryFields.all());
                        PosOrderLine posOrderLine =  posOrder.addProduct(product);
                        posOrderLine.setId(posOrderLineDao.insert(posOrderLine.toOValues()));
                        posOrder.addNewOrderLine(posOrderLine);
                        mAdapter.notifyDataSetChanged();
                        updateUI();
                        posOrderLineDao.posOrderDao.update(posOrder.getId(), posOrder.toOValues());
                        Toast.makeText(getApplicationContext(), product.getName() + " added to order", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void quickSync(final ProgressDialog progressDialog){
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ODomain oDomain = new ODomain();
                oDomain.add(Columns.server_id, "=", posOrder.getServerId());
                posOrderLineDao.posOrderDao.quickSyncRecords(oDomain);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progressDialog.dismiss();
            }
        }.execute();
    }

    private void loadActivity(PosOrder posOrder) {
        Bundle data = new Bundle();
        data.putInt(Columns.PosOrderLine.order_id, posOrder.getId());

        if(posOrder.getStatementLines().isEmpty()) {
            IntentUtils.startActivity(this, PaymentType.class, data);
        }
        else {
            IntentUtils.startActivity(this, PaymentLines.class, data);
        }

    }


}
