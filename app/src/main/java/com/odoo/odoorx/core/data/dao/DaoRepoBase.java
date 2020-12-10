package com.odoo.odoorx.core.data.dao;

import android.content.Context;
import android.util.Log;

import com.odoo.odoorx.core.base.orm.ModelRegistryUtils;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.OSQLite;
import com.odoo.odoorx.core.base.rpc.Odoo;
import com.odoo.odoorx.core.base.support.OUser;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DaoRepoBase {

    public static final String TAG = DaoRepoBase.class.getSimpleName();
    private Context context;
    private static HashMap<String, Odoo> mOdooInstances = new HashMap<>();
    private static HashMap<String, OSQLite> mSQLiteObjecs = new HashMap<>();
    private static ModelRegistryUtils modelRegistryUtils = new ModelRegistryUtils();
    private static Map<String, Map<Class, OModel>> usersOModelInstances = new HashMap<>();

    public DaoRepoBase(Context context) {
        this.context = context;
    }

    public static DaoRepoBase init(Context context) {
        if (instance == null) {
            instance = new DaoRepoBase(context);
            instance.modelRegistryUtils.makeReady(context.getApplicationContext(), context.getClass());
        }
        return instance;
    }

    private static DaoRepoBase instance;

    public static DaoRepoBase getInstance(){
        return instance;
    }

    public OSQLite getSQLite(String userName) {
        return mSQLiteObjecs.containsKey(userName) ? mSQLiteObjecs.get(userName) : null;
    }

    public void setSQLite(String userName, OSQLite sqLite) {
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

    public <T> T getModel(Context context, String modelName, OUser user) {
        Class<? extends OModel> modelCls = modelRegistryUtils.getModel(modelName);
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

    public  Context getContext(){
        return context;
    }


    public ModelRegistryUtils getModelRegistry() {
        return modelRegistryUtils;
    }

    public Map<Class, OModel> initDaoInstances(String username) {
        Log.d(TAG, "Initializing Daos  : " + username);
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
                Log.d(TAG, "Initializing Daos  : " + oModelName + " created");
            }
            usersOModelInstances.put(username, userOModelInstances);
            Log.v(TAG, "Initializing Daos  : " + username + " completed");
        }
        return userOModelInstances;
    }

    public <T> T getDao(Class<? extends OModel> klazz){
        if (context != null) {
            OUser oUser = OUser.current(context);
            String username = oUser.getUsername();
            Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
            if (userOModelInstances != null)
                return (T) userOModelInstances.get(klazz);
        }
        return null;
    }

    public boolean isDaosinitialized(String username){
        Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
        return  userOModelInstances != null && userOModelInstances.size() > 0;
    }

    public Class getModelClass(String modelName) {
        return modelRegistryUtils.getModel(modelName);
    }

    public <T> T getDao(String modelName){
        Class modelClass = getModelClass(modelName);
        OUser oUser = OUser.current(context);
        String username = oUser.getUsername();
        Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
        return (T) userOModelInstances.get(modelClass);
    }

    public void initDaos(String username){
        initDaoInstances(username);
        initDaoChildInstances(username);
    }

    public void initDaoChildInstances(String username){
        Log.d(TAG, "Child Daos         : " + " Started");
        Set<Map.Entry<String, Class<? extends OModel>>> registryModelsEntrySet = modelRegistryUtils.getModels().entrySet();
        for (Map.Entry<String, Class<? extends OModel>> entry : registryModelsEntrySet){
            Map<Class, OModel> userOModelInstances = usersOModelInstances.get(username);
            if (userOModelInstances != null) {
                OModel oModelInstance =  userOModelInstances.get(entry.getValue());
                oModelInstance.initDaos();
            }
        }
        Log.d(TAG, "Child Daos         : " + " completed");
    }

}
