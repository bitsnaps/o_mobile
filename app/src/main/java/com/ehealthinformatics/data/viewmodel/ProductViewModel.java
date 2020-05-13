package com.ehealthinformatics.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.core.orm.OValues;
import com.ehealthinformatics.core.rpc.helper.ODomain;
import com.ehealthinformatics.data.dao.CategoryDao;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dao.ProductTemplateDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dao.UoMDao;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.Category;
import com.ehealthinformatics.data.dto.Product;
import com.ehealthinformatics.data.dto.ProductTemplate;
import com.ehealthinformatics.data.dto.Uom;

import java.util.List;

public class ProductViewModel extends ViewModel {

    public static class ProductViewClass {
        public Product product;
        public List<Category> categories;
        public List<Uom> uoms;
    }
    private ProductDao productDao;
    private ProductTemplateDao productTemplateDao;
    private CategoryDao categoryDao;
    private UoMDao uoMDao;
    private final MutableLiveData<ProductViewClass> selected = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saved = new MutableLiveData<>();
    private final MutableLiveData<Boolean> synced = new MutableLiveData<>();

    public ProductViewModel(){
        this.productDao = App.getDao(ProductDao.class);
        this.categoryDao = App.getDao(CategoryDao.class);
        this.productTemplateDao = App.getDao(ProductTemplateDao.class);
        this.uoMDao = App.getDao(UoMDao.class);
    }

    public void loadData(Integer id) {
        new AsyncTask<Integer,Void, ProductViewClass>() {
            @Override
            protected ProductViewClass doInBackground(Integer... ids) {
                Product product;
                List<Category> categories;
                List<Uom> uoms;
                product = productDao.get(ids[0],  QueryFields.all());
                categories = categoryDao.getCategoryList();
                uoms = uoMDao.getUOMList();
                ProductViewClass pvc = new ProductViewClass();
                pvc.product = product;
                pvc.categories = categories;
                pvc.uoms = uoms;
                return pvc;
            }
            @Override
            protected void onPostExecute(ProductViewClass data) {
                selected.setValue(data);
            }
        }.execute(id);
    }

    public void save(OValues product, OValues productTemplate) {
        new AsyncTask<OValues, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(OValues... oValues) {
                    OValues product = oValues[0];
                    OValues productTemplate = oValues[1];
                    Product currentProduct = selected.getValue().product;
                    boolean productSaved = false;
                    boolean productTemplateSaved = false;
                    if(productTemplate.keys().size() > 0)
                        productSaved = productDao.update(currentProduct.getId(), product);
                    if(productTemplate.keys().size() > 0)
                        productTemplateSaved = productTemplateDao.update(currentProduct.getProductTemplate().getId(), productTemplate);
                    return productSaved && productTemplateSaved;
                }
            @Override
            protected void onPostExecute(Boolean data) {
                saved.setValue(data);
            }
        }.execute(product, productTemplate);
    }

    public void sync() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... oValues) {
                ODomain oDomain = new ODomain();
                oDomain.add(Columns.server_id, "=", selected.getValue().product.getServerId());
                productDao.quickSyncRecords(oDomain);
                return true;
            }

            @Override
            protected void onPostExecute(Boolean data) {
                synced.setValue(data);
            }
        }.execute();
    }

    public LiveData<ProductViewClass> getSelected() {
        return selected;
    }
    public LiveData<Boolean> getSaveStatus() {
        return saved;
    }
    public LiveData<Boolean> getSyncStatus() {
        return synced;
    }

}