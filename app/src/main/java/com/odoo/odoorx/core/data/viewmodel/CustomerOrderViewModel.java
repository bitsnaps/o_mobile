package com.odoo.odoorx.core.data.viewmodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.base.utils.BitmapUtils;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.PosOrderDao;
import com.odoo.odoorx.core.data.dao.ProductDao;
import com.odoo.odoorx.core.data.dto.CustomerOrderData;
import com.odoo.odoorx.rxshop.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerOrderViewModel extends ViewModel {
    private final HashMap<String, CustomerOrderData> cache = new HashMap<>();
    private final MutableLiveData<CustomerOrderData> productLiveData =
            new MutableLiveData<>();
    private ProductDao productDao;
    private PosOrderDao posOrderDao;
    private Context context;

    public CustomerOrderViewModel()
    {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        productDao = daoRepo.getDao(ProductDao.class);
        posOrderDao = daoRepo.getDao(PosOrderDao.class);
    }

    public LiveData<CustomerOrderData> getData() {
        return productLiveData;
    }

    public void loadOrderData(){

        new AsyncTask<Void, Void, CustomerOrderData>(){
            @Override
            protected CustomerOrderData doInBackground(Void... voids) {
                ArrayList<Bitmap> adverts = null;
                try {
                    adverts = new ArrayList<>();
                    Bitmap advert1 = BitmapUtils.getBitmap(context, R.drawable.advert1);
                    Bitmap advert2 = BitmapUtils.getBitmap(context, R.drawable.advert2);
                    Bitmap advert3 = BitmapUtils.getBitmap(context, R.drawable.advert3);
                    adverts.add(advert1);
                    adverts.add(advert2);
                    adverts.add(advert3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new CustomerOrderData(adverts);
            }

            @Override
            protected void onPostExecute(CustomerOrderData data) {
                super.onPostExecute(data);
                productLiveData.setValue(data);

            }
        }.execute();
    }

}