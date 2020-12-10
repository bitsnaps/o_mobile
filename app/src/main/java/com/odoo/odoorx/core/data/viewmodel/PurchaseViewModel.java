package com.odoo.odoorx.core.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.PurchaseOrderDao;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.dto.PurchaseOrder;

import java.util.List;

public class PurchaseViewModel extends ViewModel {
    private PurchaseOrderDao purchaseOrderDao;
    private  MutableLiveData<List<PurchaseOrder>> current = new MutableLiveData<>();

    public PurchaseViewModel(){
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        this.purchaseOrderDao = daoRepo.getDao(PurchaseOrderDao.class);
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