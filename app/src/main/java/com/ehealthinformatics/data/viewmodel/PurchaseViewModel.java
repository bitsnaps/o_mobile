package com.ehealthinformatics.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.dao.PurchaseOrderDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dto.PurchaseOrder;

import java.util.List;

public class PurchaseViewModel extends ViewModel {
    private PurchaseOrderDao purchaseOrderDao;
    private  MutableLiveData<List<PurchaseOrder>> current = new MutableLiveData<>();

    public PurchaseViewModel(){
        this.purchaseOrderDao = App.getDao(PurchaseOrderDao.class);
    }

    public void loadData() {
        new AsyncTask<Void,Void, List<PurchaseOrder>>() {
            @Override
            protected List<PurchaseOrder> doInBackground(Void... voids) {
                List<PurchaseOrder> purchaseOrders;
                purchaseOrders = purchaseOrderDao.selectAllPurchaseOrder(QueryFields.all());
                return purchaseOrders;
            }
            @Override
            protected void onPostExecute(List<PurchaseOrder> data) {
               current.setValue(data);
            }
        }.execute();
    }

    public List<PurchaseOrder> getData() {
        return current.getValue();
    }

}