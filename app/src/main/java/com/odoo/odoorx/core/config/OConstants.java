package com.odoo.odoorx.core.config;

public class OConstants {
    public static final String URL_ODOO = "https://www.odoo.com"; // should install POS addon
    public static final String URL_ODOO_RESET_PASSWORD = URL_ODOO + "/web/reset_password";
    public static final String URL_ODOO_SIGN_UP = URL_ODOO + "/web/signup";
    public static final String URL_ODOO_MOBILE_GIT_HUB = "https://github.com/JohnTheBeloved/o_mobile";
    public static final String URL_ODOO_APPS_ON_PLAY_STORE = "https://play.google.com/store/apps/developer?id=Odoo+SA";


    public static final int RPC_REQUEST_TIME_OUT = 30000; // 30 Seconds
    public static final int RPC_REQUEST_RETRIES = 3; // Retries when timeout
    public static final String ODOO_DATABASES = "database1,database2";
    public static String CURRENCY_SYMBOL = " ";

    /**
     * Database version. Required to
     * change in increment order
     * when you change your database model in case of released apk.
     *
     * When dealing with DATABASE_VERSION, you need to override onModelUpgrade() method
     * in each of the modelName class for applying upgrade script for that model.
     */
    public static final int DATABASE_VERSION = 1;

    public static Integer DATA_SYNC_LIMIT = 9000;
    public static Integer DATA_SYNC_DEPTH = 8;

    public static int getSyncLimit(String modelName){
        return DATA_SYNC_LIMIT;
    }

    public static int getSyncDepth(String modelName){
        return DATA_SYNC_DEPTH;
    }

    public static boolean alwaysSyncRelations(String modelName) {
        if(modelName.equalsIgnoreCase("pos.session"))
            return true;
        else return false;
    }

    public static int getAppCompany() {
        return 3;
    }
}
