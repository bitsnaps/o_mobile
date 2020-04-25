package com.ehealthinformatics.app.fragment;

import androidx.lifecycle.Observer;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.ehealthinformatics.app.listeners.OnMoreButtonClickListener;
import com.ehealthinformatics.core.utils.BitmapUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.lifecycle.ViewModelProviders;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.viewmodel.PosOrderListViewModel;
import com.ehealthinformatics.R;
import com.ehealthinformatics.app.activity.shopping.PosOrderCart;
import com.ehealthinformatics.app.listeners.OnItemClickListener;
import com.ehealthinformatics.data.adapter.PosOrderListAdapter;
import com.ehealthinformatics.data.dao.PosOrderDao;
import com.ehealthinformatics.data.dto.PosOrder;
import com.ehealthinformatics.core.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.ehealthinformatics.core.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.core.support.drawer.ODrawerItem;
import com.ehealthinformatics.core.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PosOrderList22 extends BaseFragment implements ISyncStatusObserverListener,
       SwipeRefreshLayout.OnRefreshListener, IOnSearchViewChangeListener, View.OnClickListener,
        OnItemClickListener<PosOrder> {

    public static final String TAG = PosOrderList.class.getSimpleName();
    public static final String KEY = PosOrderList.class.getSimpleName();
    private PosOrderListAdapter mAdapter = null;
    private RecyclerView mRecyclerView;
    private PosOrderListViewModel posOrderListViewModel;
    PosOrderDao posOrderDao;
    private FilterSelect filterKeys;
    private boolean syncRequested = false;
    private FloatingActionButton fab;
    private Button fib;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        posOrderDao = App.getDao(PosOrderDao.class);
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls(view);
        initList();
        initDataLoad();
    }

    private void initControls(View view){
        //setHasSwipeRefreshView(view, R.id.swipe_container, this);
        setHasFloatingButton(view, R.id.fab_new_item, mRecyclerView, this);
        //setHasFilterButton(view, R.id.fab_new_item, mRecyclerView, this);
        //setSwipeRefreshing(true);
    }

    private void initList(){
        posOrderListViewModel = ViewModelProviders.of(this).get(PosOrderListViewModel.class);
        mAdapter = new PosOrderListAdapter(getContext(),this, new OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, PosOrder posOrder, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_pos_order_sync_now: {
                        setSwipeRefreshing(true);
                        posOrderListViewModel.quickSync(posOrder);
                        Toast.makeText(getContext(), posOrder.getName() + " (" + item.getTitle() + ") clicked", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.action_pos_order_delete: {
                        setSwipeRefreshing(true);
                        posOrderDao.delete(posOrder.getId(), true);
                        Toast.makeText(getContext(), posOrder.getName() + " (" + item.getTitle() + ") clicked", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                     }
        });
        mRecyclerView = getActivity().findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initDataLoad(){
        posOrderListViewModel.getData().observe(this, new Observer<List<PosOrder>>() {
            @Override
            public void onChanged(@Nullable List<PosOrder> posOrderList) {
                mAdapter.changeList(posOrderList);
                hideRefreshingProgress();
            }
        });
    }

    @Override
    public Class<PosOrderDao> database() {
        return PosOrderDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Sale Orders")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new PosOrderList()));
        return items;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(PosOrderDao.AUTHORITY);
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
        inflater.inflate(R.menu.menu_customer_dashboard, menu);
        setHasSearchView(this, menu, R.id.menu_product_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        setSwipeRefreshing(true);
        posOrderListViewModel.searchFilter(newFilter != null ? newFilter : "");
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.fab_new_item){
            Bundle bundle = new Bundle();
            bundle.putBoolean(IntentUtils.IntentParams.EDIT_MODE, true);
            IntentUtils.startActivity(getActivity(), PosOrderCart.class, bundle);
        } else if(v.getId() == R.id.btn_filter_template){
            setSwipeRefreshing(true);
            Button button = ((Button)v);
            Selection selection = filterKeys.next();
            posOrderListViewModel.onStateChange(selection.key);
            button.setText(selection.value);
        }

    }

    public static class FilterSelect{

        public int index;
        public   Map<String, String> selections;
        public List<String> keys;

        public FilterSelect(Map selectionMap){
            index = -1;
            selections = selectionMap;
            selections.put("all", "All");
            keys = new ArrayList<>(selectionMap.keySet());
        }

        public Selection next(){
            if(index < keys.size() -1){
                index++;

            }else {
                index = 0;
            }
            String key = keys.get(index);
            return  new Selection(key, selections.get(key));
        }
    }

    public static class Selection{
        Selection(String key, String value){
            this.key = key;
            this.value = value;
        }
        public String key;
        public String value;
    }

    @Override
    public void onItemClick(View view, PosOrder posOrder, int position) {
        Bundle data = new Bundle();
        data.putInt("pos_order_id", posOrder.getId());
        IntentUtils.startActivity(getContext(), PosOrderCart.class, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        posOrderListViewModel.loadData();
    }
}
