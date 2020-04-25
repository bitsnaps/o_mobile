/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 30/12/14 3:28 PM
 */
package com.ehealthinformatics.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ehealthinformatics.App;
import com.ehealthinformatics.R;
import com.ehealthinformatics.app.listeners.OnItemClickListener;
import com.ehealthinformatics.config.OConstants;
import com.ehealthinformatics.core.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.ehealthinformatics.core.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.core.support.drawer.ODrawerItem;
import com.ehealthinformatics.core.utils.DateUtils;
import com.ehealthinformatics.core.utils.IntentUtils;
import com.ehealthinformatics.core.utils.ODateUtils;
import com.ehealthinformatics.data.adapter.CustomerListAdapter;
import com.ehealthinformatics.data.dao.IrModel;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dao.ResPartner;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.Customer;
import com.ehealthinformatics.data.dto.SyncMode;
import com.ehealthinformatics.data.dto.SyncModel;
import com.ehealthinformatics.data.viewmodel.CustomerLazyListViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomerList2 extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener,
 IOnSearchViewChangeListener, View.OnClickListener,
        OnItemClickListener<Customer> {

    public static final String TAG = ResPartner.class.getSimpleName();
    public static final String KEY = CustomerList2.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private boolean syncRequested = false;
    private CustomerListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private CustomerLazyListViewModel viewModel;
    private RelativeLayout noItemLayout;
    private LinearLayout itemsLayout;
    private RelativeLayout rlLoading;
    private LinearLayout llSyncProgress;
    private TextView tvSyncProgress;
    private ProgressBar pbSyncProgress;
    private TextView tvProcess;
    private CustomerList.Type mType = CustomerList.Type.Customer;

    //Action Views
    Button activeFilterButton;

    //Filter tokens
    private String activeFilter = "";



    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.srl_items, this);
        mView = view;
        recyclerView = view.findViewById(R.id.rv_list);
        mAdapter = new CustomerListAdapter(getContext(), this);
        setHasFloatingButton(view, R.id.fab_new_item, recyclerView, this);
        initControls(view);
        initListeners();
        initList();
        initDataLoad();
    }


    private void initControls(View view){
//        activeFilterButton = getActivity().findViewById(R.id.btn_filter_template);
//        activeFilterButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active, 0, 0,0);
//        activeFilterButton.setText(getString(R.string.active));
        noItemLayout = view.findViewById(R.id.rl_empty_list);
        itemsLayout = view.findViewById(R.id.ll_items_list);
        llSyncProgress = view.findViewById(R.id.ll_sync_progress);
        tvSyncProgress = view.findViewById(R.id.tv_sync_progress);
        pbSyncProgress = view.findViewById(R.id.pb_sync_progress);
        rlLoading = view.findViewById(R.id.rl_loading);

    }

    private void initListeners() {

//        activeFilterButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Button button = ((Button)v);
//                String text = button.getText().toString();
//                String inactive = getString(R.string.inactive);
//                String active = getString(R.string.active);
//                String all = getString(R.string.all);
//                if (text.equals(active)) {
//                    text = inactive;
//                    activeFilter = inactive;
//                    setButtonTint(R.color.colorAccent);
//                }else if(text.equals(inactive)){
//
//                    text = all;
//                    activeFilter = all;
//                    setButtonTint(R.color.colorPrimaryGrey);
//                }else if( text.equals(all)){
//                    text = active;
//                    activeFilter = active;
//                    setButtonTint(R.color.colorPrimaryWhite);
//                }
//                button.setText(text);
//                reload();
//            }
//        });
    }

    private void initList(){
        viewModel = ViewModelProviders.of(this).get(CustomerLazyListViewModel.class);
        recyclerView = getActivity().findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void initDataLoad(){
        viewModel.getData().observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable List<Customer> productList) {
                mAdapter.reset(productList);
                listLoaded(productList.isEmpty());
            }
        });
    }

    private void isLoading(){
        resetLayout();
        rlLoading.setVisibility(View.VISIBLE);
    }

    private void isSyncing(){
        resetLayout();
        llSyncProgress.setVisibility(View.VISIBLE);
    }

    private void resetLayout(){
        rlLoading.setVisibility(View.GONE);
        noItemLayout.setVisibility(View.GONE);
        itemsLayout.setVisibility(View.GONE);
        llSyncProgress.setVisibility(View.GONE);}

    private void listLoaded(Boolean isEmpty) {
        resetLayout();
        if (isEmpty) {
            noItemLayout.setVisibility(View.VISIBLE);
            itemsLayout.setVisibility(View.GONE);
        } else {
            noItemLayout.setVisibility(View.GONE);
            itemsLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public Class<ResPartner> database() {
        return ResPartner.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Customers")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new CustomerList2()));

        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        if(!refreshing){
            loadCustomers();
        }
    }

    private void loadCustomers(){
        isLoading();
        viewModel.loadAllCustomers();
    }

    private void syncCustomers(SyncModel syncModel){
        isSyncing();
        requestSync();
        Log.v(TAG, "Customer Refresh    : " + " Refresh triggerIn Progress " +  syncModel.getLocalCount() + "/" + syncModel.getServerCount() );
        Log.v(TAG, "Customer Refresh    : " + " Last updated " +  syncModel.getLastSynced());

    }


    @Override
    public void onRefresh() {
        final Handler handler = new Handler();
        final int delay = 50; //milliseconds
        final AtomicInteger noOfRuns = new AtomicInteger(1);

        handler.postDelayed(new Runnable(){
            public void run(){
                IrModel syncModelDao = App.getDao(IrModel.class);
                SyncModel syncModel = syncModelDao.getOrCreateTrigger(ModelNames.PRODUCT, Columns.SyncModel.Mode.REFRESH_TRIGGERED,OConstants.getSyncDepth(ModelNames.PRODUCT), QueryFields.all());
                Date last5Minutes = ODateUtils.getDateMinuteBefore(DateUtils.now(), 5);
                boolean updatedLessThan5MinsAgo = syncModel.getProcessUpdated().after(last5Minutes);
                boolean createdLessThan5MinsAgo = syncModel.getLastSynced().after(last5Minutes);
                if(createdLessThan5MinsAgo && syncModel.isQueued()){
                    syncCustomers(syncModel);
                    displaySyncInfo(noOfRuns.get(), syncModel);
                    handler.postDelayed(this, delay);
                }else{
                    if(updatedLessThan5MinsAgo){

                        if(syncModel.isCompleted()){
                            loadCustomers();
                        } else {
                            displaySyncInfo(noOfRuns.get(), syncModel);
                            handler.postDelayed(this, delay);
                        }
                    }else {
                        if(syncModel.isCompleted() && (noOfRuns.get() == 1 || noOfRuns.get() % 10 == 0) || syncModel.isQueued()) {
                            syncCustomers(syncModel);
                            displaySyncInfo(noOfRuns.get(), syncModel);
                        }else {
                            displaySyncInfo(noOfRuns.get(), syncModel);
                            handler.postDelayed(this, delay);
                        }
                    }
                }

                noOfRuns.addAndGet(1);
            }
        }, noOfRuns.get() == 1 ? 1000 : delay);

    }


    private  void displaySyncInfo(int noOfRetries, SyncModel syncModel){
        isSyncing();
        SyncModel.SyncProgressCount syncProgressCount = syncModel.getChildCount();
        tvSyncProgress.setText("" +
                " Trying to load items (" + noOfRetries + " times) " +
                syncProgressCount.getCount() + "/" + syncProgressCount.getTotal() + "\n" + syncModel.getStatusDetail());
        pbSyncProgress.setProgress(syncModel.realPercentage());
    }

    private void requestSync(){
        if (inNetwork()) {
            parent().sync().requestSync(ResPartner.AUTHORITY);
        } else {
            hideRefreshingProgress();
                Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners_2, menu);
        setHasSearchView(this, menu, R.id.menu_common_list_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        isLoading();
        if(newFilter != null && !newFilter.isEmpty() &&  newFilter.length() > 3){
            mCurFilter = newFilter;
            showRefreshingProgress();
            viewModel.searchFilter(newFilter);
            return true;
        } else {
            viewModel.loadAllCustomers();
            return false;
        }

    }

    @Override
    public void onSearchViewClose() {
       viewModel.loadAllCustomers();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabButton:
//            loadActivity(null);
//            break;
//        }
    }

    private void loadActivity(Customer customer) {
        Bundle data = new Bundle();
        data.putInt(Columns.id, customer.getId());

        data.putString(CustomerDetails.KEY_PARTNER_TYPE, mType.toString());
        IntentUtils.startActivity(getActivity(), CustomerDetails.class, data);
    }



    @Override
    public void onItemClick(View v, Customer customer, int pos) {
        loadActivity(customer);
    }

    //Last Vendor Price
    //Buying Frequency
    //Average VEndor Price
    //Lowest Vendor Price
}
