package com.odoo.rxshop.fragment;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.odoorx.rxshop.R;
import com.odoo.odoorx.rxshop.data.adapter.PurchaseOrderListAdapter;
import com.odoo.odoorx.core.data.dao.PurchaseOrderDao;
import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.BaseFragment;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.odoorx.rxshop.base.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.odoorx.core.base.support.drawer.ODrawerItem;
import com.odoo.odoorx.core.base.utils.IntentUtils;
import com.odoo.odoorx.core.data.viewmodel.PosOrderListViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchaseList extends BaseFragment implements ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener,
IOnSearchViewChangeListener, View.OnClickListener,
AdapterView.OnItemClickListener {

    public static final String TAG = PurchaseList.class.getSimpleName();
    public static final String KEY = PurchaseList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private Date mDateFilter = null;
    private boolean syncRequested = false;
    private DataSetObserver dataSetObserver;
    final Calendar myCalendar = Calendar.getInstance();
    private PurchaseOrderListAdapter mAdapter = null;
    private PosOrderListViewModel viewModel;
    Button dateFilter;
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateFilter.setText(sdf.format(myCalendar.getTime()));
        mDateFilter = myCalendar.getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        View view =  inflater.inflate(R.layout.common_listview, container, false);
        dateFilter =
                (Button) getActivity().findViewById(R.id.btn_filter_template);
        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        setTitle("Purchases");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
         dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

            }
        };
        mView = view;
        ListView mProductsList = (ListView) view.findViewById(R.id.rv_list);
        mAdapter = new PurchaseOrderListAdapter();

       mProductsList.setFastScrollAlwaysVisible(true);
        mProductsList.setOnItemClickListener(this);


    }

    @Override
    public Class<PurchaseOrderDao> database() {
        return PurchaseOrderDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("PurchaseList")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new PurchaseList()));

        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        viewModel.loadData();
    }


    @Override
    public void onRefresh() {
        if (inNetWork()) {
            mDateFilter = null;
            parent().sync().requestSync(PurchaseOrderDao.AUTHORITY);
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
        mCurFilter = newFilter;

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

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), PurchaseDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
//        loadActivity(row);
    }

    public static String getFormattedDate(Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        //2nd of march 2015
        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
            switch (day % 10) {
                case 1:
                    return new SimpleDateFormat("E, id'st' 'of' MMMM yyyy").format(date);
                case 2:
                    return new SimpleDateFormat("E, id'nd' 'of' MMMM yyyy").format(date);
                case 3:
                    return new SimpleDateFormat("E, id'rd' 'of' MMMM yyyy").format(date);
                default:
                    return new SimpleDateFormat("E, id'th' 'of' MMMM yyyy").format(date);
            }
        return new SimpleDateFormat("E, id'th' 'of' MMMM yyyy").format(date);
    }

}
