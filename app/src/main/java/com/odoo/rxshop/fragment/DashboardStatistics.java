package com.odoo.rxshop.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.odoo.odoorx.core.data.dto.Dashboard;
import com.odoo.odoorx.rxshop.R;
import com.odoo.rxshop.activity.shopping.PosOrderCart;
import com.odoo.odoorx.core.base.utils.IntentUtils;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.BaseFragment;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.odoorx.core.base.support.drawer.ODrawerItem;
import com.odoo.odoorx.core.data.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class DashboardStatistics extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY = DashboardStatistics.class.getSimpleName();

    DashboardViewModel viewModel;
    Dashboard dashboardData;

    TextView tvUserWelcome, tvTotalCustomers, tvTotalOrders, tvTotalProducts, tvTotalPayments, tvSyncNow,
            tvSessionId, tvOpeningBalance, tvClosingBalance, tvOpeningDate;
    ImageButton ibNewOrder;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.activity_dashboard_statistics, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.nested_scroll_view, this);
        initUI(view);
        initListener();
        initDataLoad();
    }

    private void  initUI(View view) {
        tvTotalCustomers = view.findViewById(R.id.tv_dashstat_customers);
        tvTotalOrders = view.findViewById(R.id.tv_dashstat_orders);
        tvTotalProducts = view.findViewById(R.id.tv_dashstat_products);
        tvTotalPayments = view.findViewById(R.id.tv_dashstat_payments);
        tvUserWelcome = view.findViewById(R.id.tv_dash_welcome);
        tvSyncNow = view.findViewById(R.id.tv_dash_sync_now);
        ibNewOrder = view.findViewById(R.id.ib_new_order);
        tvSessionId = view.findViewById(R.id.tv_current_session);
        tvOpeningBalance =  view.findViewById(R.id.tv_dash_opening_balance);
        tvClosingBalance =  view.findViewById(R.id.tv_dash_closing_balance);
    }

    private void initListener() {
        ibNewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(IntentUtils.IntentParams.EDIT_MODE, true);
                IntentUtils.startActivity(getActivity(), PosOrderCart.class, bundle);
            }
        });
        tvSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.startActivity(getActivity(), PosOrderList.class, null);
            }
        });

    }

    private void initDataLoad(){
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);

        viewModel.getDashboardData().observe(this, new Observer<Dashboard>() {
            @Override
            public void onChanged(@Nullable Dashboard dashboardData) {
                DashboardStatistics.this.dashboardData = dashboardData;
                updateUI();
            }
        });
        viewModel.loadDashboardData();
    }

    private void updateUI(){
        tvUserWelcome.setText("Welcome " + dashboardData.getUser().getPartner().getDisplayName());
        tvTotalCustomers.setText(dashboardData.getNoOfCustomers() +"");
        tvTotalProducts.setText(dashboardData.getNoOfProducts() +"");
        tvTotalOrders.setText(dashboardData.getNoOfOrders() +"");
        tvTotalPayments.setText(dashboardData.getTotalPayments() +"");
        tvSessionId.setText(dashboardData.getPosSession().getName());
        tvTotalOrders.setText(dashboardData.getNoOfOrders());
        tvOpeningBalance.setText(dashboardData.getOpeningBalance());
        tvClosingBalance.setText(dashboardData.getClosingBalance());
        hideRefreshingProgress();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadDashboardData();
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//        } else {
//            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Dashboard")
                .setIcon(R.drawable.ic_business)
                .setInstance(new DashboardStatistics()));
        return  items;
    }

    @Override
    public <T> Class<T> database() {
        return null;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        viewModel.loadDashboardData();
    }

    @Override
    public void onRefresh() {
        viewModel.loadDashboardData();
    }


    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New POS Session?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

}
