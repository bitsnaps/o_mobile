package com.ehealthinformatics.data.viewmodel;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.R;
import com.ehealthinformatics.core.utils.BitmapUtils;
import com.ehealthinformatics.data.dao.PosOrderDao;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dto.CustomerOrderData;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerOrderViewModel extends ViewModel {
    private final HashMap<String, CustomerOrderData> cache = new HashMap<>();
    private final MutableLiveData<CustomerOrderData> productLiveData =
            new MutableLiveData<>();
    private ProductDao productDao;
    private PosOrderDao posOrderDao;

    public CustomerOrderViewModel()
    {
        productDao = App.getDao(ProductDao.class);
        posOrderDao = App.getDao(PosOrderDao.class);
    }

    public LiveData<CustomerOrderData> getData() {
        return productLiveData;
    }

    public void loadOrderData(){

        new AsyncTask<Void, Void, CustomerOrderData>(){
            @Override
            protected CustomerOrderData doInBackground(Void... voids) {
                ArrayList<Bitmap> adverts = new ArrayList<>();
                Bitmap advert1 = BitmapUtils.getBitmap(App.getContext(), R.drawable.advert1);
                Bitmap advert2 = BitmapUtils.getBitmap(App.getContext(), R.drawable.advert2);
                Bitmap advert3 = BitmapUtils.getBitmap(App.getContext(), R.drawable.advert3);
                adverts.add(advert1);
                adverts.add(advert2);
                adverts.add(advert3);
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