package com.odoo.odoorx.core.base.orm;

import android.content.Context;

import com.odoo.odoorx.core.base.support.OUser;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;

import dalvik.system.DexFile;

public class ModelRegistryUtils {

    private HashMap<String, Class<? extends OModel>> models = new HashMap<>();

    public void makeReady(Context context, Class clazz) {
        try {
            DexFile dexFile = new DexFile(context.getPackageCodePath());
//            Log.d("DEBUG", "Creating models for package: " + context.getPackageCodePath());
            for (Enumeration<String> item = dexFile.entries(); item.hasMoreElements(); ) {
                String element = item.nextElement();
                if (element.startsWith(clazz.getPackage().getName())) {
                    Class<? extends OModel> clsName = (Class<? extends OModel>) Class.forName(element);
                    if (clsName != null && clsName.getSuperclass() != null &&
                            OModel.class.isAssignableFrom(clsName.getSuperclass())) {
//                        Log.d("DEBUG", "Creating model for: " + clsName);
                        String modelName = getModelName(context, clsName);
                        if (modelName != null) {
                            this.models.put(modelName, clsName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getModelName(Context context, Class cls) {
        try {
            Constructor constructor = cls.getConstructor(Context.class, OUser.class);
            OModel model = (OModel) constructor.newInstance(context, null);
            return model.getModelName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<? extends OModel> getModel(String modelName) {
        if (models.containsKey(modelName)) {
            return models.get(modelName);
        }
        return null;
    }

    public HashMap<String, Class<? extends OModel>> getModels() {
        return models;
    }
}
