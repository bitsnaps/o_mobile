package com.odoo.odoorx.core.data.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.dao.ResPartner;
import com.odoo.odoorx.core.data.dto.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerListViewModel extends ViewModel {
    private final MutableLiveData<List<Customer>>  customerLiveData =
            new MutableLiveData<>();
    private ResPartner customerDao;

    public CustomerListViewModel()
    {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        customerDao = daoRepo.getDao(ResPartner.class);
    }

    public LiveData<List<Customer>> getData() {
        return  customerLiveData;
    }

    public void loadAll(){

        new AsyncTask<Void, Void, List<Customer>>(){
            @Override
            protected List<Customer> doInBackground(Void...voids) {
                return new ArrayList<>();// customerDao.selectAll(CustomerList.Type.Customer);
            }

            @Override
            protected void onPostExecute(List<Customer>  customerList) {
                super.onPostExecute( customerList);
                 customerLiveData.setValue(customerList);
            }
        }.execute();
    }


    public void searchFilter(final String filterText){

        new AsyncTask<String, Void, List<Customer>>(){
            @Override
            protected List<Customer> doInBackground(String... searchText) {
                return customerDao.searchFilter(searchText[0], QueryFields.all());
            }

            @Override
            protected void onPostExecute(List<Customer>  customerList) {
                super.onPostExecute( customerList);
                customerLiveData.setValue(customerList);
            }
        }.execute(filterText);
    }

}