package com.ehealthinformatics.rxshop.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthinformatics.odoorx.rxshop.R;
import com.ehealthinformatics.odoorx.rxshop.data.adapter.AdvertismentListAdapter;
import com.ehealthinformatics.rxshop.activity.shopping.PosOrderCart;
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener;
import com.ehealthinformatics.odoorx.rxshop.base.support.addons.fragment.BaseFragment;
import com.ehealthinformatics.odoorx.core.base.support.drawer.ODrawerItem;
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils;
import com.ehealthinformatics.odoorx.core.data.dao.PosOrderDao;
import com.ehealthinformatics.odoorx.core.data.dto.CustomerOrderData;
import com.ehealthinformatics.odoorx.core.data.dto.PosOrder;
import com.ehealthinformatics.odoorx.core.data.viewmodel.CustomerOrderViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomerWelcomePage extends BaseFragment implements  View.OnClickListener,
        OnItemClickListener<PosOrder> {

    public static final String TAG = CustomerWelcomePage.class.getSimpleName();
    public static final String KEY = CustomerWelcomePage.class.getSimpleName();
    private AdvertismentListAdapter adapAdvertisement = null;
    private CustomerOrderViewModel viewModel;
    private EditText etSearchProduct;
    private Button btnUploadPresription;
    private ImageButton ibRefil;
    private ImageButton ibReminder;
    private ImageButton ibQuestion;
    private RecyclerView rvAdvertisement;
    //private RecyclerView rvCategory;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.layout_customer_order, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls(view);
        initAdapters(view);
        initDataLoad();
        viewModel.loadOrderData();
    }

    private void initControls(View v){
        //etSearchProduct = v.findViewById(R.id.et_customer_order_search);
        rvAdvertisement = v.findViewById(R.id.rv_advertisement);
        btnUploadPresription = v.findViewById(R.id.btn_customer_order_upload_prescription);
        //rvCategory = v.findViewById(R.id.rv_category);
    }

    private void initAdapters(View v) {
        viewModel = ViewModelProviders.of(this).get(CustomerOrderViewModel.class);
        adapAdvertisement = new AdvertismentListAdapter();
        rvAdvertisement = v.findViewById(R.id.rv_advertisement);
        rvAdvertisement.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAdvertisement.setItemAnimator(new DefaultItemAnimator());
        rvAdvertisement.setAdapter(adapAdvertisement);
    }

    private void initDataLoad(){
        viewModel.getData().observe(this, new Observer<CustomerOrderData>() {
            @Override
            public void onChanged(@Nullable CustomerOrderData customerOrderData) {
                adapAdvertisement.resetList(customerOrderData.getAdverts());
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
        items.add(new ODrawerItem(KEY).setTitle("New Order")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new CustomerWelcomePage()));
        return items;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_customer_dashboard, menu);
        //setHasSearchView(this, menu, R.id.menu_product_search);
        MenuItem cartCount = menu.findItem(R.id.menu_notification);
        //MenuItemCompat.setActionView(cartCount, R.layout.layout_cart_icon);
        RelativeLayout notifiationLayout = (RelativeLayout) MenuItemCompat.getActionView(cartCount);
        TextView tv = (TextView) notifiationLayout.findViewById(R.id.tv_cart_count);
        tv.setText("12");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
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
//            Selection selection = filterKeys.next();
//            posOrderListViewModel.onStateChange(selection.key);
//            button.setText(selection.value);
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

}
