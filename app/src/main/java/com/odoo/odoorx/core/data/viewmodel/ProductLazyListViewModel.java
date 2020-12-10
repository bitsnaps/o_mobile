package com.odoo.odoorx.core.data.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.data.LazyList;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.IrModel;
import com.odoo.odoorx.core.data.dao.ProductDao;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Product;
import com.odoo.odoorx.core.data.dto.SyncModel;

import java.util.ArrayList;
import java.util.List;

public class ProductLazyListViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> productLiveData =
            new MutableLiveData<>();
    private ProductDao productDao;
    IrModel syncModelDao;

    public ProductLazyListViewModel()
    {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        productDao = daoRepo.getDao(ProductDao.class);
        syncModelDao =  daoRepo.getDao(IrModel.class);
        loadAllProducts();
    }

    public LiveData<List<Product>> getData() {
        return productLiveData;
    }

    public void loadAllProducts(){

        new AsyncTask<Void, Void, List<Product>>(){
            @Override
            protected List<Product> doInBackground(Void... voids) {
                //TODO: Make this light
              final SyncModel syncModel =  syncModelDao.getOrCreateTrigger(ModelNames.PRODUCT, Columns.SyncModel.Mode.REFRESH_TRIGGERED,5, QueryFields.all());
              if(syncModel != null && syncModel.isCompleted()){
                  return productDao.selectAll(QueryFields.all());
              }

              //TODO: Other way to return empty lazy list
              return new LazyList<>(new LazyList.ItemFactory<Product>() {
                  @Override
                  public Product create(int id) {
                      return null;
                  }
              }, new ArrayList<ODataRow>());
            }

            @Override
            protected void onPostExecute(List<Product> productList) {
                super.onPostExecute(productList);
                productLiveData.setValue(productList);
            }
        }.execute();
    }


    public void searchFilter(final String filterText){

        new AsyncTask<String, Void, LazyList<Product>>(){
            @Override
            protected LazyList<Product> doInBackground(String... searchText) {
                return productDao.lazySearchFilter(filterText, QueryFields.all());
            }

            @Override
            protected void onPostExecute(LazyList<Product> productList) {
                super.onPostExecute(productList);
                productLiveData.setValue(productList);
            }
        }.execute();
    }


}