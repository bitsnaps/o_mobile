package com.odoo.addons.abirex.list;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.abirex.form.PosOrderDetails;
import com.odoo.base.addons.abirex.adapter.PosOrderListAdapter;
import com.odoo.base.addons.abirex.dao.PosOrderDao;
import com.odoo.base.addons.abirex.model.PosOrder;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.utils.IntentUtils;
import com.odoo.data.LazyList;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PosOrderList extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<LazyList<PosOrder>>, SwipeRefreshLayout.OnRefreshListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener, PosOrderListAdapter.ContextMenuCallback {

    public static final String TAG = PosOrderList.class.getSimpleName();
    public static final String KEY = PosOrderList.class.getSimpleName();
    private View mView;
    private PosOrderDao posOrderDao;
    private PosOrderListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private boolean syncRequested = false;


    //Action Views
    Button activeFilterButton;

    //Filter tokens
    private String activeFilter = "";

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.layout_customer_product_list, container, false);
    }

    private void setupView(){
        activeFilterButton =
                (Button) getActivity().findViewById(R.id.filter_template);
        activeFilterButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active, 0, 0,0);
        activeFilterButton.setText(getString(R.string.active));

        activeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = ((Button)v);
                String text = button.getText().toString();
                String inactive = getString(R.string.inactive);
                String active = getString(R.string.active);
                String all = getString(R.string.all);
                if (text.equals(active)) {
                    text = inactive;
                    activeFilter = inactive;
                    setButtonTint(R.color.colorAccent);
                }else if(text.equals(inactive)){

                    text = all;
                    activeFilter = all;
                    setButtonTint(R.color.colorPrimaryGrey);
                }else if( text.equals(all)){
                    text = active;
                    activeFilter = active;
                    setButtonTint(R.color.colorPrimaryWhite);
                }
                button.setText(text);
                reload();
            }
        });
        mAdapter = new PosOrderListAdapter(this);
        setListStyle();

    }

    private void setListStyle(){
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private void reload(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setButtonTint(int color){
        DrawableCompat.setTint(activeFilterButton.getCompoundDrawables()[0],
                ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        getLoaderManager().initLoader(0, null, this);
        setupView();
    }

    @Override
    public Loader<LazyList<PosOrder>> onCreateLoader(int id, Bundle data) {
        posOrderDao = App.getDao(PosOrderDao.class);
        Loader<LazyList<PosOrder>> lazyListLoader = (Loader<LazyList<PosOrder>>) posOrderDao.selectAll();
        return lazyListLoader;
    }

    @Override
    public void onLoadFinished(Loader<LazyList<PosOrder>> lazyListLoader, LazyList<PosOrder> productLazyList) {

        mAdapter.changeList(productLazyList);
        if (productLazyList.size() > 0) {
            TextView listTitle = (TextView) mView.findViewById(R.id.list_title);
            Log.d(TAG, "Count is...." + productLazyList.size());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    OControls.setGone(mView, R.id.loadingProgress);
//                    OControls.setVisible(mView, R.id.swipe_container);
//                    OControls.setGone(mView, R.id.data_list_no_item);
//                    setHasSwipeRefreshView(mView, R.id.swipe_container, PosOrderList.this);

                }
            }, 500);
        } else {
            Log.d(TAG, "None Count is...." + productLazyList.size());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    OControls.setGone(mView, R.id.loadingProgress);
//                    OControls.setGone(mView, R.id.swipe_container);
//                    OControls.setVisible(mView, R.id.data_list_no_item);
//                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, PosOrderList.this);
//                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_products);
//                    OControls.setText(mView, R.id.title, _s(R.string.label_no_product_found));
//                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LazyList<PosOrder>> lazyListLoader) {
        mAdapter.resetList();
    }

    @Override
    public Class<PosOrderDao> database() {
        return PosOrderDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("PosOrder Category")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new PosOrderList()));

        return items;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(PosOrderDao.AUTHORITY);
            setSwipeRefreshing(true);
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
        inflater.inflate(R.menu.menu_customer_product, menu);
        setHasSearchView(this, menu, R.id.menu_cart);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabButton:
//            loadActivity(null);
//            break;
//        }
    }

    private void loadActivity(PosOrder posOrder) {
        Bundle data = new Bundle();
        if (posOrder != null) {
            data.putInt("pos_order_id", posOrder.getId());
        }
        IntentUtils.startActivity(getActivity(), PosOrderDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        PosOrder posOrder = mAdapter.getItem(position);
        loadActivity(posOrder);
    }

    @Override
    public void onContextMenuClick(@NotNull ImageButton view, int id, @NotNull String title) {

    }
}
