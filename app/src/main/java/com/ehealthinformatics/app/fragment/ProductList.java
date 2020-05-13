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
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ehealthinformatics.App;
import com.ehealthinformatics.app.activity.product.ProductEdit;
import com.ehealthinformatics.app.listeners.OnItemClickListener;
import com.ehealthinformatics.config.OConstants;
import com.ehealthinformatics.core.service.SyncStatus;
import com.ehealthinformatics.core.utils.DateUtils;
import com.ehealthinformatics.core.utils.IntentUtils;
import com.ehealthinformatics.core.utils.ODateUtils;
import com.ehealthinformatics.data.dao.IrModel;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.Product;
import com.ehealthinformatics.data.dto.SyncMode;
import com.ehealthinformatics.data.dto.SyncModel;
import com.ehealthinformatics.data.viewmodel.ProductLazyListViewModel;
import com.ehealthinformatics.R;
import com.ehealthinformatics.data.adapter.ProductListAdapter;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.core.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.ehealthinformatics.core.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.core.support.drawer.ODrawerItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener,
 IOnSearchViewChangeListener, View.OnClickListener,
        OnItemClickListener<Product> {

    public static final String TAG = ProductDao.class.getSimpleName();
    public static final String KEY = ProductList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private boolean syncRequested = false;
    private ProductListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private ProductLazyListViewModel viewModel;
    private LinearLayout noItemRefreshLayout;
    private LinearLayout itemsRefreshLayout;
    private LinearLayout llLoading;
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
        mAdapter = new ProductListAdapter(this);
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
        noItemRefreshLayout = view.findViewById(R.id.rl_empty_list);
        itemsRefreshLayout = view.findViewById(R.id.ll_items_list);
        llSyncProgress = view.findViewById(R.id.ll_sync_progress);
        tvSyncProgress = view.findViewById(R.id.tv_sync_progress);
        pbSyncProgress = view.findViewById(R.id.pb_sync_progress);
        llLoading = view.findViewById(R.id.rl_loading);

    }

    private void initListeners() {

    }

    private void initList(){
        viewModel = ViewModelProviders.of(this).get(ProductLazyListViewModel.class);
        recyclerView = getActivity().findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void initDataLoad(){
        viewModel.getData().observe(this, new Observer<List<Product>>() {
            @Override
            public void onChanged(@Nullable final List<Product> productList) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.changeList(productList);
                        listLoaded(productList.isEmpty());
                    }
                }, 3000);

            }
        });
    }

    private void isLoading(){
        resetLayout();
        llLoading.setVisibility(View.VISIBLE);
    }

    private void isSyncing(){
        resetLayout();
        llSyncProgress.setVisibility(View.VISIBLE);
    }

    private void resetLayout(){
        llLoading.setVisibility(View.GONE);
        noItemRefreshLayout.setVisibility(View.GONE);
        itemsRefreshLayout.setVisibility(View.GONE);
        llSyncProgress.setVisibility(View.GONE);}

    private void listLoaded(Boolean isEmpty) {
        hideRefreshingProgress();
        resetLayout();
        if (isEmpty) {
            noItemRefreshLayout.setVisibility(View.VISIBLE);
            itemsRefreshLayout.setVisibility(View.GONE);
        } else {
            noItemRefreshLayout.setVisibility(View.GONE);
            itemsRefreshLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public Class<ProductDao> database() {
        return ProductDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Products")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new ProductList()));

        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        if(!refreshing){
            loadProducts();
        }
    }

    private void loadProducts(){
        isLoading();
        viewModel.loadAllProducts();
    }

    private void syncProducts(SyncModel syncModel){
        isSyncing();
        requestSync();
        Log.v(TAG, "Product Refresh    : " + " Refresh triggerIn Progress " +  syncModel.getLocalCount() + "/" + syncModel.getServerCount() );
        Log.v(TAG, "Product Refresh    : " + " Last updated " +  syncModel.getLastSynced());

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
                    if(syncModel.isQueued()){
                        syncProducts(syncModel);
                        displaySyncInfo(noOfRuns.get(), syncModel);
                        handler.postDelayed(this, delay);
                    }
                }else{
                    if(updatedLessThan5MinsAgo){

                        if(syncModel.isCompleted()){
                            Toast.makeText(getContext(), "Will sync with Server sync in " + DateUtils.stringDifference(last5Minutes, syncModel.getProcessUpdated()), Toast.LENGTH_SHORT).show();
                            loadProducts();
                        } else {
                            displaySyncInfo(noOfRuns.get(), syncModel);
                            handler.postDelayed(this, delay);
                        }
                    }else {
                        if(syncModel.isCompleted() && (noOfRuns.get() == 1 || noOfRuns.get() % 10 == 0) || syncModel.isQueued()) {
                            syncProducts(syncModel);
                            syncModel.requeue();
                            displaySyncInfo(noOfRuns.get(), syncModel);
                            handler.postDelayed(this, delay);
                        }else if(!syncModel.isCompleted() && noOfRuns.get() > 10) {
                            syncProducts(syncModel);
                            displaySyncInfo(noOfRuns.get(), syncModel);
                            handler.postDelayed(this, delay);
                        }
                        else {
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
        tvSyncProgress.setText(getStatusDetail(syncModel));


//                "" +
//                " Trying to load items (" + noOfRetries + " times) " +
//                syncProgressCount.getCount() + "/" + syncProgressCount.getTotal() + "\n" + syncModel.getStatusDetail());

        pbSyncProgress.setProgress(syncModel.realPercentage());
    }

    private  String getStatusDetail(SyncModel syncModel){
        String statusDetail = syncModel.getStatusDetail();
        if(syncModel.getStatus().equals(SyncStatus.CHILD_SYNC_CREATED)){
            String  childStatusDetail = "";
            Iterator<SyncModel> syncModelIterator  = syncModel.getChildren();
            while(syncModelIterator.hasNext()){
                SyncModel childSyncModel = syncModelIterator.next();
                if(!childSyncModel.isQueued() && !childSyncModel.isCompleted()){
                    statusDetail = getStatusDetail(childSyncModel);
                    return statusDetail + "\n\t" + childStatusDetail;
                }
            }
        }
        return statusDetail;
    }



    private void requestSync(){
        if (inNetwork()) {
            parent().sync().requestSync(ProductDao.AUTHORITY);
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
            case R.id.menu_common_list_delete_all: {
                    deleteAll();

            }
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
            viewModel.loadAllProducts();
            return false;
        }

    }

    @Override
    public void onSearchViewClose() {
       viewModel.loadAllProducts();
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabButton:
//            loadActivity(null);
//            break;
//        }
    }

    private void loadActivity(Product product) {
        Bundle data = new Bundle();
        data.putInt(IntentUtils.IntentParams.ID, product.getId());
        IntentUtils.startActivity(getActivity(), ProductEdit.class, data);
    }



    @Override
    public void onItemClick(View v, Product product, int pos) {
        loadActivity(product);
    }

    public void deleteAll(){
        isLoading();
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... nothing) {
                ProductDao productDao = App.getDao(ProductDao.class);

                productDao.delete("id != ?",  new String[]{0+""}, true);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean productList) {
                super.onPostExecute(productList);
                loadProducts();
            }
        }.execute();
    }


    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadAllProducts();
    }


}
