/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 17/12/14 6:06 PM
 */
package com.odoo;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.odoo.base.addons.abirex.Defaults;
import com.odoo.base.addons.abirex.dao.AccountDao;
import com.odoo.base.addons.abirex.dao.LocationDao;
import com.odoo.base.addons.abirex.dao.PosConfigDao;
import com.odoo.base.addons.abirex.dao.PosOrderDao;
import com.odoo.base.addons.abirex.dao.PosOrderLineDao;
import com.odoo.base.addons.abirex.dao.PosSessionDao;
import com.odoo.base.addons.abirex.dao.PriceListDao;
import com.odoo.base.addons.abirex.dao.ProductDao;
import com.odoo.base.addons.abirex.dao.ProductTemplateDao;
import com.odoo.base.addons.abirex.dao.PurchaseOrderDao;
import com.odoo.base.addons.abirex.dao.PurchaseOrderLineDao;
import com.odoo.base.addons.abirex.dao.StockPickingDao;
import com.odoo.base.addons.abirex.dao.StockPickingTypeDao;
import com.odoo.base.addons.abirex.dto.Company;
import com.odoo.base.addons.abirex.dto.ProductTemplate;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.ModelRegistryUtils;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.ModelNames;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class App extends Application {

    public static final String TAG = App.class.getSimpleName();
    public static String APPLICATION_NAME;
    private static HashMap<String, Odoo> mOdooInstances = new HashMap<>();
    private static HashMap<String, OSQLite> mSQLiteObjecs = new HashMap<>();
    private static ModelRegistryUtils modelRegistryUtils = new ModelRegistryUtils();

    @Override
    public void onCreate() {
        super.onCreate();
        App.APPLICATION_NAME = getPackageManager().getApplicationLabel(getApplicationInfo()).toString();
        App.modelRegistryUtils.makeReady(getApplicationContext());
        mContext = this;
    }

    public static OSQLite getSQLite(String userName) {
        return mSQLiteObjecs.containsKey(userName) ? mSQLiteObjecs.get(userName) : null;
    }

    public static void setSQLite(String userName, OSQLite sqLite) {
        mSQLiteObjecs.put(userName, sqLite);
    }

    public Odoo getOdoo(OUser user) {
        if (mOdooInstances.containsKey(user.getAndroidName())) {
            return mOdooInstances.get(user.getAndroidName());
        }
        return null;
    }

    public void setOdoo(Odoo odoo, OUser user) {
        if (user != null)
            mOdooInstances.put(user.getAndroidName(), odoo);
    }

    /**
     * Checks for network availability
     *
     * @return true, if network available
     */
    public boolean inNetwork() {
        boolean isConnected = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = manager.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnectedOrConnecting()) {
            isConnected = true;
        }
        return isConnected;
    }

    /**
     * Checks for installed application
     *
     * @param appPackage
     * @return true, if application installed on device
     */
    public boolean appInstalled(String appPackage) {
        boolean mInstalled = false;
        try {
            PackageManager mPackage = getPackageManager();
            mPackage.getPackageInfo(appPackage, PackageManager.GET_ACTIVITIES);
            mInstalled = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mInstalled;
    }

    public static <T> T getModel(Context context, String modelName, OUser user) {
        Class<? extends OModel> modelCls = App.modelRegistryUtils.getModel(modelName);
        if (modelCls != null) {
            try {
                Constructor constructor = modelCls.getConstructor(Context.class, OUser.class);
                return (T) constructor.newInstance(context, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


        private static Context mContext;



        public static Context getContext(){
            return mContext;
        }


    public ModelRegistryUtils getModelRegistry() {
        return modelRegistryUtils;
    }

    private static Map<String, Map<Class, OModel>> usersOModelInstances = new HashMap<>();

    private static  Map<Class, OModel> initDaoInstances(String username) {
        Set<Map.Entry<String, Class<? extends OModel>>> registryModelsEntrySet = modelRegistryUtils.getModels().entrySet();
        Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
        if(userOModelInstances == null){
            userOModelInstances = new HashMap<>();
            for (Map.Entry<String, Class<? extends OModel>> entry : registryModelsEntrySet){
                String oModelName = entry.getKey();
                Class<? extends OModel> oModelClass = entry.getValue();
                OModel oModelInstance = OModel.get(getContext(), oModelName, username);
                oModelInstance.prepareFields();
                userOModelInstances.put(oModelClass, oModelInstance);
            }
            usersOModelInstances.put(username, userOModelInstances);
        }
        return userOModelInstances;
    }

    public static <T> T getDao(Class<? extends OModel> klazz){
        Context context = getContext();
        if (context != null) {
            OUser oUser = OUser.current(getContext());
            String username = oUser != null ? oUser.getUsername() : "topzy20@yahoo.com";
            Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
            if (userOModelInstances != null)
                return (T) userOModelInstances.get(klazz);
        }
        return null;
   }

//   private static Map<String, Class> modelMap = new HashMap<String, Class>() {{
//       put(ModelNames.ACCOUNT, AccountDao.class);
//       put(ModelNames.STOCK_LOCATION, LocationDao.class);
//       put(ModelNames.POS_CONFIG, PosConfigDao.class);
//       put(ModelNames.POS_ORDER, PosOrderDao.class);
//       put(ModelNames.POS_ORDER_LINE, PosOrderLineDao.class);
//       put(ModelNames.POS_SESSION, PosSessionDao.class);
//       put(ModelNames.PRODUCT_PRICELIST, PriceListDao.class);
//       put((ModelNames.PRODUCT_TEMPLATE, ProductTemplateDao.class);
//       put((ModelNames.PURCHASE_ORDER, PurchaseOrderDao.class);
//       put((ModelNames.POS_ORDER_LINE, PurchaseOrderLineDao.class);
//       put((ModelNames.STOCK_PICKING, StockPickingDao.class);
//       put((ModelNames.STOCK_PICKING_TYPE, StockPickingTypeDao.class);
//   }};

   private static Class getModelClass(String modelName) {
        return modelRegistryUtils.getModel(modelName);
   }

    public static <T> T getDao(String modelName){
        Class modelClass = getModelClass(modelName);
        OUser oUser = OUser.current(getContext());
        String username = oUser.getUsername();
        Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
        return (T) userOModelInstances.get(modelClass);
    }

   public static void initDaos(String username){
       initDaoInstances(username);
       initDaoChildInstances(username);
       Log.i(TAG,"Daos initialised...");
   }

   private static void initDaoChildInstances(String username){
       Set<Map.Entry<String, Class<? extends OModel>>> registryModelsEntrySet = modelRegistryUtils.getModels().entrySet();
       for (Map.Entry<String, Class<? extends OModel>> entry : registryModelsEntrySet){
           Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
           if (userOModelInstances != null) {
               OModel oModelInstance =  userOModelInstances.get(entry.getValue());
               oModelInstance.initDaos();
           }
       }
   }

   public static Company getCompany(){
       ResCompany companyDao = getDao(ResCompany.class);
       return companyDao.get(companyDao.selectRowId(Defaults.getAppCompany()));
   }

}
