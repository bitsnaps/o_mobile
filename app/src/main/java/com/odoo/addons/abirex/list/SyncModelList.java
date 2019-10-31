package com.odoo.addons.abirex.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.App;
import com.odoo.R;
import com.odoo.base.addons.abirex.adapter.SyncModelsListAdapter;
import com.odoo.base.addons.ir.IrModel;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;


import java.util.ArrayList;
import java.util.List;

public class SyncModelList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    public static final String TAG = SyncModelList.class.getSimpleName();
    public static final String KEY = SyncModelList.class.getSimpleName();
    private IrModel irModel;
    private SyncModelsListAdapter mAdapter = null;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.layout_sync_model_list, container, false);
    }

    private void setupView() {
        irModel = App.getDao(IrModel.class);
        mAdapter = new SyncModelsListAdapter(parent().sync());
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_sync_model);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        setupView();
    }

    @Override
    public Class<IrModel> database() {
        return IrModel.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Sync Models")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new SyncModelList()));
        return items;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
    }

    @Override
    public void onRefresh() {
        setSwipeRefreshing(true);
        mAdapter.changeList( irModel.selectDTOs());
        hideRefreshingProgress();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_customer_product, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btnSyncTrigger:
//                parent().sync().requestSync(ProductDao.AUTHORITY);
//            break;
//        }
    }



}
