package com.odoo.odoorx.core.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.base.orm.OValues;
import com.odoo.odoorx.core.base.orm.RelValues;
import com.odoo.odoorx.core.base.rpc.helper.ODomain;
import com.odoo.odoorx.core.data.dao.CategoryDao;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.ProductDao;
import com.odoo.odoorx.core.data.dao.ProductTemplateDao;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.dao.UoMDao;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.Category;
import com.odoo.odoorx.core.data.dto.Product;
import com.odoo.odoorx.core.data.dto.Uom;
import com.odoo.odoorx.core.data.util.SaveState;

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
    private final MutableLiveData<SaveState> saved = new MutableLiveData<>();
    private final MutableLiveData<Boolean> synced = new MutableLiveData<>();

    public ProductViewModel(){
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        this.productDao = daoRepo.getDao(ProductDao.class);
        this.categoryDao = daoRepo.getDao(CategoryDao.class);
        this.productTemplateDao = daoRepo.getDao(ProductTemplateDao.class);
        this.uoMDao = daoRepo.getDao(UoMDao.class);
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
        new AsyncTask<OValues, Void, SaveState>() {
                @Override
                protected SaveState doInBackground(OValues... oValues) {
                    RelValues relValues = new RelValues();
                    OValues product = oValues[0];
                    OValues productTemplate = oValues[1];
                    Product currentProduct = selected.getValue().product;
                    boolean saveProduct = product.keys().size() > 0;
                    boolean saveProductTemplate = productTemplate.keys().size() > 0;
                    boolean productSaved = false;
                    boolean productTemplateSaved = false;
                    if (saveProduct)
                        productSaved = productDao.update(currentProduct.getId(), product);
                    if(saveProductTemplate)
                        productTemplateSaved = productTemplateDao.update(currentProduct.getProductTemplate().getId(), productTemplate);
                    if (saveProduct || saveProductTemplate) {
                        if (saveProductTemplate && productTemplateSaved == false) {
                            return SaveState.getErrorState("Product Template not save succesfully");
                        }
                        if (saveProduct && productSaved == false) {
                            return SaveState.getErrorState("Product not save succesfully");
                        }
                        return SaveState.getSavedState();
                    } else {
                        return SaveState.getEmptyDataState();
                    }
                }
            @Override
            protected void onPostExecute(SaveState data) {
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
    public LiveData<SaveState> getSaveStatus() {
        return saved;
    }
    public LiveData<Boolean> getSyncStatus() {
        return synced;
    }

}