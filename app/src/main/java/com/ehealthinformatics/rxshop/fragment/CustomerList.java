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
package com.ehealthinformatics.rxshop.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener;
import com.ehealthinformatics.odoorx.rxshop.data.adapter.CustomerListAdapter;
import com.ehealthinformatics.odoorx.core.data.dao.ResPartner;
import com.ehealthinformatics.odoorx.core.base.orm.ODataRow;
import com.ehealthinformatics.odoorx.rxshop.base.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.odoorx.rxshop.base.support.addons.fragment.IOnSearchViewChangeListener;
import com.ehealthinformatics.odoorx.rxshop.base.support.addons.fragment.ISyncStatusObserverListener;
import com.ehealthinformatics.odoorx.core.base.support.drawer.ODrawerItem;
import com.ehealthinformatics.odoorx.core.base.support.list.OCursorListAdapter;
import com.ehealthinformatics.odoorx.core.base.utils.BitmapUtils;
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils;
import com.ehealthinformatics.odoorx.core.base.utils.OControls;
import com.ehealthinformatics.odoorx.core.data.dto.Customer;
import com.ehealthinformatics.odoorx.core.data.viewmodel. CustomerListViewModel;

import java.util.ArrayList;
import java.util.List;

public class CustomerList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String KEY = CustomerList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private boolean syncRequested = false;
    private CustomerListAdapter mAdapter = null;
    private  CustomerListViewModel viewModel;
    private RecyclerView mRecyclerView;

    public enum Type {
        Customer, Supplier, Company
    }

    private Type mType = Type.Customer;

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
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        mType = Type.valueOf(getArguments().getString(EXTRA_KEY_TYPE));
        initControls(view);
        initList();
        initDataLoad();
    }

    private void initControls(View view){
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        setHasFloatingButton(view, R.id.fab_new_item, mRecyclerView, this);
        setHasFilterButton(view, R.id.fab_new_item, mRecyclerView, this);
        setSwipeRefreshing(true);
    }


    private void initList(){
        viewModel = ViewModelProviders.of(this).get(CustomerListViewModel.class);
        mAdapter = new CustomerListAdapter(getContext(), new OnItemClickListener<Customer>() {
            @Override
            public void onItemClick(View v, Customer posOrderLine, int pos) {

            }
        });
        mRecyclerView = getActivity().findViewById(R.id.rv_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initDataLoad(){
        viewModel.getData().observe(this, new Observer<List<Customer>>() {
            @Override
            public void onChanged(@Nullable List<Customer> posOrderList) {
                mAdapter.reset(posOrderList);
                hideRefreshingProgress();
            }
        });
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("name"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.image_small, img);
        OControls.setText(view, R.id.name, row.getString("name"));
        OControls.setText(view, R.id.company_name, (row.getString("company_name").equals("false"))
                ? "" : row.getString("company_name"));
        OControls.setText(view, R.id.email, (row.getString("email").equals("false") ? " "
                : row.getString("email")));
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
//        String where = "";
//        List<String> args = new ArrayList<>();
//        switch (mType) {
//            case Customer:
//                where = "customer = ?";
//                break;
//            case Supplier:
//                where = "supplier = ?";
//                break;
//            case Company:
//                where = "is_company = ?";
//                break;
//        }
//        args.add("true");
//        if (mCurFilter != null) {
//            where += " and name like ? ";
//            args.add(mCurFilter + "%");
//        }
//        String selection = (args.size() > 0) ? where : null;
//        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
//        return new CursorLoader(getActivity(), db().uri(),
//                null, selection, selectionArgs, "name");
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAdapter.changeCursor(data);
//        if (data.getLocal_count() > 0) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    OControls.setGone(mView, R.id.loadingProgress);
//                    OControls.setVisible(mView, R.id.swipe_container);
//                    OControls.setGone(mView, R.id.data_list_no_item);
//                    setHasSwipeRefreshView(mView, R.id.swipe_container, CustomerList.this);
//                }
//            }, 500);
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    OControls.setGone(mView, R.id.loadingProgress);
//                    OControls.setGone(mView, R.id.swipe_container);
//                    OControls.setVisible(mView, R.id.data_list_no_item);
//                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, CustomerList.this);
//                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
//                    OControls.setText(mView, R.id.title, _s(R.string.label_no_customer_found));
//                    OControls.setText(mView, R.id.subTitle, "");
//                }
//            }, 500);
//            if (db().isEmptyTable() && !syncRequested) {
//                syncRequested = true;
//                onRefresh();
//            }
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        mAdapter.changeCursor(null);
//    }

    @Override
    public Class<ResPartner> database() {
        return ResPartner.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("CustomerList")
                .setIcon(R.drawable.ic_action_customers)
                .setExtra(extra(Type.Customer))
                .setInstance(new CustomerList()));
        items.add(new ODrawerItem(KEY).setTitle("Suppliers")
                .setIcon(R.drawable.ic_action_suppliers)
                .setExtra(extra(Type.Supplier))
                .setInstance(new CustomerList()));
        items.add(new ODrawerItem(KEY).setTitle("Companies")
                .setIcon(R.drawable.ic_action_company)
                .setExtra(extra(Type.Company))
                .setInstance(new CustomerList()));
        return items;
    }

    public Bundle extra(Type type) {
        Bundle extra = new Bundle();
        extra.putString(EXTRA_KEY_TYPE, type.toString());
        return extra;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        viewModel.loadAll();
    }


    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(ResPartner.AUTHORITY);
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
        inflater.inflate(R.menu.menu_partners, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
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
        data.putString(CustomerDetails.KEY_PARTNER_TYPE, mType.toString());
        IntentUtils.startActivity(getActivity(), CustomerDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
//        loadActivity(row);
    }
}
