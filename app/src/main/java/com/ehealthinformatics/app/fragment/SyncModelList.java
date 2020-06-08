package com.ehealthinformatics.app.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ehealthinformatics.App;
import com.ehealthinformatics.R;
import com.ehealthinformatics.data.adapter.SyncModelsListAdapter;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dao.IrModel;
import com.ehealthinformatics.core.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.core.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.core.support.drawer.ODrawerItem;
import com.ehealthinformatics.app.utils.LineItemDecoration;


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
        recyclerView = getActivity().findViewById(R.id.rv_sync_models);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(getContext(), LinearLayout.VERTICAL));
        //recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        setupView();
        onRefresh();
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
        mAdapter.changeList( irModel.selectDTOs(Columns.SyncModel.Mode.REFRESH_TRIGGERED, QueryFields.all()));
        hideRefreshingProgress();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_customer_dashboard, menu);
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
