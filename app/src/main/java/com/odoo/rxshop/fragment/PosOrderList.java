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
package com.odoo.rxshop.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.odoo.RxShop;
import com.odoo.odoorx.core.data.dao.AccountBankStatementLineDao;
import com.odoo.rxshop.activity.shopping.PosOrderCart;
import com.odoo.rxshop.listeners.OnItemClickListener;
import com.odoo.rxshop.listeners.OnMoreButtonClickListener;
import com.odoo.odoorx.core.config.OConstants;
import com.odoo.odoorx.core.base.utils.DateUtils;
import com.odoo.odoorx.core.base.utils.IntentUtils;
import com.odoo.odoorx.core.base.utils.ODateUtils;
import com.odoo.odoorx.core.data.dao.IrModel;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.PosOrder;
import com.odoo.odoorx.core.data.dto.SyncModel;
import com.odoo.odoorx.rxshop.R;
import com.odoo.odoorx.rxshop.data.adapter.PosOrderListAdapter;
import com.odoo.odoorx.core.data.dao.PosOrderDao;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.BaseFragment;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.odoorx.core.base.support.drawer.ODrawerItem;
import com.odoo.odoorx.core.data.viewmodel.PosOrderListViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PosOrderList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener,
        IOnSearchViewChangeListener, View.OnClickListener,
        OnItemClickListener<PosOrder>, OnMoreButtonClickListener {

    public static final String TAG = PosOrderDao.class.getSimpleName();
    public static final String KEY = PosOrderList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private boolean syncRequested = false;
    private PosOrderListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private PosOrderListViewModel viewModel;
    private RelativeLayout noItemRefreshLayout;
    private LinearLayout itemsRefreshLayout;
    private RelativeLayout rlLoading;
    private LinearLayout llSyncProgress;
    private TextView tvSyncProgress;
    private ProgressBar pbSyncProgress;
    private TextView tvProcess;

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
        mAdapter = new PosOrderListAdapter(getContext(), this, this);
        setHasFloatingButton(view, R.id.fab_new_item, recyclerView, this);
        initControls(view);
        initListeners();
        initList();
        initDataLoad();
    }


    private void initControls(View view){
        noItemRefreshLayout = view.findViewById(R.id.rl_empty_list);
        itemsRefreshLayout = view.findViewById(R.id.ll_items_list);
        llSyncProgress = view.findViewById(R.id.ll_sync_progress);
        tvSyncProgress = view.findViewById(R.id.tv_sync_progress);
        pbSyncProgress = view.findViewById(R.id.pb_sync_progress);
        rlLoading = view.findViewById(R.id.rl_loading);

    }

    private void initListeners() {

    }

    private void initList(){
        viewModel = ViewModelProviders.of(this).get(PosOrderListViewModel.class);
        recyclerView = getActivity().findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void initDataLoad(){
        viewModel.getData().observe(this, new Observer<List<PosOrder>>() {
            @Override
            public void onChanged(@Nullable final List<PosOrder> productList) {
                isLoading();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.changeList(productList);
                        listLoaded(productList.isEmpty());
                    }
                }, 500);
            }
        });
    }

    private void isLoading(){
        resetLayout();
        if (rlLoading != null) {
            rlLoading.setVisibility(View.VISIBLE);
        }
    }

    private void isSyncing(){
        resetLayout();
        llSyncProgress.setVisibility(View.VISIBLE);
    }

    private void resetLayout(){
        if (rlLoading != null) {
            rlLoading.setVisibility(View.GONE);
        }
        if (noItemRefreshLayout != null) {
            noItemRefreshLayout.setVisibility(View.GONE);
        }
        if (itemsRefreshLayout != null) {
            itemsRefreshLayout.setVisibility(View.GONE);
        }
        if (llSyncProgress != null){
            llSyncProgress.setVisibility(View.GONE);
        }
    }

    private void listLoaded(Boolean isEmpty) {
        resetLayout();
        if (isEmpty) {
            if (noItemRefreshLayout != null) {
                noItemRefreshLayout.setVisibility(View.VISIBLE);
            }
            if (itemsRefreshLayout != null) {
                itemsRefreshLayout.setVisibility(View.GONE);
            }
        } else {
            if (noItemRefreshLayout != null)
                noItemRefreshLayout.setVisibility(View.GONE);
            if (itemsRefreshLayout != null)
                itemsRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public Class<PosOrderDao> database() {
        return PosOrderDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("POS Orders")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new PosOrderList()));

        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        if(!refreshing){
            loadPosOrders();
        }
    }

    private void loadPosOrders(){
        isLoading();
        viewModel.loadData();
    }

    private void syncPosOrders(SyncModel syncModel){
        isSyncing();
        requestSync();
        Log.v(TAG, "PosOrder Refresh    : " + " Refresh triggerIn Progress " +  syncModel.getLocalCount() + "/" + syncModel.getServerCount() );
        Log.v(TAG, "PosOrder Refresh    : " + " Last updated " +  syncModel.getLastSynced());

    }


    @Override
    public void onRefresh() {
        new Handler()
                .postDelayed(new Runnable(){
            public void run(){
               loadPosOrders();
            }
        }, 1000 );

    }

    private void fef(Handler handler){
        final int delay = 50; //milliseconds
        final AtomicInteger noOfRuns = new AtomicInteger(1);



        IrModel syncModelDao = RxShop.getDao(IrModel.class);
        SyncModel syncModel = syncModelDao.getOrCreateTrigger(ModelNames.POS_ORDER, Columns.SyncModel.Mode.REFRESH_TRIGGERED, OConstants.getSyncDepth(ModelNames.PRODUCT),  QueryFields.all());
        Date last5Minutes = ODateUtils.getDateMinuteBefore(DateUtils.now(), 5);
        boolean updatedLessThan5MinsAgo = syncModel.getProcessUpdated().after(last5Minutes);
        boolean createdLessThan5MinsAgo = syncModel.getLastSynced().after(last5Minutes);
        if(createdLessThan5MinsAgo && syncModel.isQueued()){
            syncPosOrders(syncModel);
            displaySyncInfo(noOfRuns.get(), syncModel);
            //handler.postDelayed(this, delay);
        }else{
            if(updatedLessThan5MinsAgo){

                if(syncModel.isCompleted()){
                    loadPosOrders();
                } else {
                    displaySyncInfo(noOfRuns.get(), syncModel);
                   // handler.postDelayed(this, delay);
                }
            }else {
                if(syncModel.isCompleted() && (noOfRuns.get() == 1 || noOfRuns.get() % 10 == 0) || syncModel.isQueued()) {
                    syncPosOrders(syncModel);
                    displaySyncInfo(noOfRuns.get(), syncModel);
                }else {
                    displaySyncInfo(noOfRuns.get(), syncModel);
                   // handler.postDelayed(this, delay);
                }
            }
        }
        noOfRuns.addAndGet(1);
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
        if (inNetWork()) {
//            parent().sync().requestSync(PosOrderDao.AUTHORITY);
            parent().sync().requestSync(AccountBankStatementLineDao.AUTHORITY);
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
            viewModel.loadData();
            return false;
        }

    }

    @Override
    public void onSearchViewClose() {
        viewModel.loadData();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabButton:
//            loadActivity(null);
//            break;
//        }
    }

    @Override
    public void onItemClick(View view, PosOrder posOrder, int position) {
        Bundle data = new Bundle();
        data.putInt(IntentUtils.IntentParams.ID, posOrder.getId());
        data.putBoolean(IntentUtils.IntentParams.EDIT_MODE, posOrder.getServerId() == 0);
        IntentUtils.startActivity(getContext(), PosOrderCart.class, data);
    }

    @Override
    public void onItemClick(View view, PosOrder posOrder, MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_pos_order_sync_now: {
                setSwipeRefreshing(true);
                viewModel.quickSync(posOrder);
                Toast.makeText(getContext(), posOrder.getName() + " (" + item.getTitle() + ") clicked", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.action_pos_order_delete: {
                setSwipeRefreshing(true);
                //posOrderDao.delete(posOrder.getId(), true);
                Toast.makeText(getContext(), posOrder.getName() + " (" + item.getTitle() + ") clicked", Toast.LENGTH_SHORT).show();
                break;
            }


        }
    }


    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadData();
    }


    //Last Vendor Price
    //Buying Frequency
    //Average VEndor Price
    //Lowest Vendor Price
}
