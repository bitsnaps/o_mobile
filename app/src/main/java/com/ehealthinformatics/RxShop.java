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
package com.ehealthinformatics;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ehealthinformatics.odoorx.core.base.auth.OUserAccount;
import com.ehealthinformatics.odoorx.core.base.orm.ModelRegistryUtils;
import com.ehealthinformatics.odoorx.core.base.orm.OModel;
import com.ehealthinformatics.odoorx.core.base.orm.OSQLite;
import com.ehealthinformatics.odoorx.core.base.rpc.Odoo;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.data.dao.DaoRepoBase;
import com.facebook.stetho.Stetho;

import java.util.Map;

public class RxShop extends Application {

    public static final String TAG = RxShop.class.getSimpleName();
    public static String APPLICATION_NAME;
    private static DaoRepoBase daoRepo;
    private static OUserAccount oUserAccount;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        RxShop.APPLICATION_NAME = getPackageManager().getApplicationLabel(getApplicationInfo()).toString();
        context = this;
        Stetho.initializeWithDefaults(this);
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

    public static void initDaos(OUserAccount userAccount){
        daoRepo = DaoRepoBase.init(context);
        oUserAccount = userAccount;
        daoRepo.initDaos(oUserAccount.getOUser().getUsername());

    }

    public static <T> T getDao(Class<? extends OModel> klazz){
        return daoRepo.getDao(klazz);
   }
    public static <T> T getDao(String modelName){
        return daoRepo.getDao(modelName);
    }

   public static Context getAppContext(){
        return context;
   }

   public static Odoo getOdoo() {
        return oUserAccount.getOdoo();
   }

}
