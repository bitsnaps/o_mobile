package com.odoo.odoorx.core.data.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.data.LazyList;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.IrModel;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.dao.ResPartner;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Customer;
import com.odoo.odoorx.core.data.dto.SyncModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerLazyListViewModel extends ViewModel {
    private final HashMap<String, List<Customer>> cache = new HashMap<>();
    private final MutableLiveData<LazyList<Customer>> customerLiveData =
            new MutableLiveData<>();
    private ResPartner customerDao;
    IrModel syncModelDao;

    public CustomerLazyListViewModel()
    {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        customerDao = daoRepo.getDao(ResPartner.class);
        syncModelDao =  daoRepo.getDao(IrModel.class);
        loadAllCustomers();
    }

    public LiveData<LazyList<Customer>> getData() {
        return customerLiveData;
    }

    public void loadAllCustomers(){

        new AsyncTask<Void, Void, LazyList<Customer>>(){
            @Override
            protected LazyList<Customer> doInBackground(Void... voids) {
                //TODO: Make this light
              final SyncModel syncModel =  syncModelDao.getOrCreateTrigger(ModelNames.PARTNER, Columns.SyncModel.Mode.REFRESH_TRIGGERED,5, QueryFields.all());
              if(syncModel != null && syncModel.isCompleted()){
                  return customerDao.lazySelectAll(QueryFields.all());
              }

              //TODO: Other way to return empty lazy list
              return new LazyList<>(new LazyList.ItemFactory<Customer>() {
                  @Override
                  public Customer create(int id) {
                      return null;
                  }
              }, new ArrayList<ODataRow>());
            }

            @Override
            protected void onPostExecute(LazyList<Customer> customerList) {
                super.onPostExecute(customerList);
                customerLiveData.setValue(customerList);
            }
        }.execute();
    }


    public void searchFilter(final String filterText){

        new AsyncTask<String, Void, LazyList<Customer>>(){
            @Override
            protected LazyList<Customer> doInBackground(String... searchText) {
                return customerDao.lazySelectAll(QueryFields.all());
            }

            @Override
            protected void onPostExecute(LazyList<Customer> customerList) {
                super.onPostExecute(customerList);
                customerLiveData.setValue(customerList);
            }
        }.execute();
    }

}