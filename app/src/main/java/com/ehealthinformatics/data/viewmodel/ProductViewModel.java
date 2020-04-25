package com.ehealthinformatics.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dto.Product;

public class ProductViewModel extends ViewModel {
    private ProductDao productDao;
    private final MutableLiveData<Product> selected = new MutableLiveData<>();

    public ProductViewModel(){
        this.productDao = App.getDao(ProductDao.class);
    }

    public void loadData() {
        new AsyncTask<Integer,Void, Product>() {
            @Override
            protected Product doInBackground(Integer... ids) {
                Product product;
                product = productDao.get(ids[0],  QueryFields.all());
                return product;
            }
            @Override
            protected void onPostExecute(Product data) {
                selected.setValue(data);
            }
        }.execute();
    }

    public void get(Integer id) {
        new AsyncTask<Integer,Void, Product>() {
            @Override
            protected Product doInBackground(Integer... ids) {
                Product product;
                product = productDao.get(ids[0],  QueryFields.all());
                return product;
            }
            @Override
            protected void onPostExecute(Product data) {
                selected.setValue(data);
            }
        }.execute(id);
    }

    public void save(Integer id) {
        if(selected.getValue().getId() == id) {
            new AsyncTask<Integer, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Integer... ids) {
                    Product product = selected.getValue();
                    return productDao.update(ids[0], product.toOValues());
                }

            }.execute(id);
        }
    }

    public LiveData<Product> getSelected() {
        return selected;
    }

}