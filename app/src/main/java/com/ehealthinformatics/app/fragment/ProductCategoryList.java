package com.ehealthinformatics.app.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProviders;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.ehealthinformatics.app.listeners.OnItemClickListener;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dto.Product;
import com.ehealthinformatics.data.viewmodel.ProductLazyListViewModel;
import com.ehealthinformatics.R;
import com.ehealthinformatics.data.adapter.ProductListAdapter;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.ehealthinformatics.core.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.core.support.drawer.ODrawerItem;
import com.ehealthinformatics.core.utils.IntentUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, IOnSearchViewChangeListener, View.OnClickListener,
        OnItemClickListener<Product> {

    public static final String TAG = ProductCategoryList.class.getSimpleName();
    public static final String KEY = ProductCategoryList.class.getSimpleName();
    private View mView;
    private ProductDao productDao;
    private ProductListAdapter mAdapter = null;
    private RecyclerView recyclerView;
    private boolean syncRequested = false;
    private ProductLazyListViewModel viewModel;


    //Action Views
    Button activeFilterButton;
    ImageButton ibListStyle;

    //
    int viewStyle = 1;


    //Filter tokens
    private String activeFilter = "";


    private void initList(){
        viewModel = ViewModelProviders.of(this).get(ProductLazyListViewModel.class);
        recyclerView = getActivity().findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.layout_customer_product_list, container, false);
    }


    private void setupView(){
        activeFilterButton =
                (Button) getActivity().findViewById(R.id.btn_filter_template);
        ibListStyle =
                (ImageButton) getActivity().findViewById(R.id.ib_list_style);
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
        ibListStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // setListStyle(viewStyle == 1 ? ++viewStyle : --viewStyle);
            }
        });
        mAdapter = new ProductListAdapter(this);
        //setListStyle(viewStyle);
    }

//    private void setListStyle(int style){
//        recyclerView = (RecyclerView) getActivity().findViewById(R.id.rv_customer_product);
//        if(style == 1) {
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            mAdapter.changeToGrid(false);
//            ibListStyle.setImageResource(R.drawable.ic_view_module_black_24dp);
//        }
//        else if(style == 2){
//            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//            mAdapter.changeToGrid(true);
//            ibListStyle.setImageResource(R.drawable.ic_view_list_black_24dp);
//        }
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(mAdapter);
//        //registerForContextMenu(recyclerView);
//
//    }

    private void reload(){
        setSwipeRefreshing(true);
        viewModel.loadAllProducts();
    }

    private void setButtonTint(int color){
        DrawableCompat.setTint(activeFilterButton.getCompoundDrawables()[0],
                ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        initList();
        setupView();
    }

    @Override
    public Class<ProductDao> database() {
        return ProductDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Product Category")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new ProductCategoryList()));
        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        viewModel.loadAllProducts();
    }


    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(ProductDao.AUTHORITY);
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
        inflater.inflate(R.menu.menu_customer_dashboard, menu);
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
        setSwipeRefreshing(true);
        viewModel.searchFilter(newFilter);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_new_item:
            loadActivity(null);
            break;
        }
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), ProductDetails.class, data);
    }


    @Override
    public void onItemClick(View v, Product item, int pos) {
        //        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
//        loadActivity(row);
    }
}
