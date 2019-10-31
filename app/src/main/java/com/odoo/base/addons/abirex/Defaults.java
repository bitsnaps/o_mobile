package com.odoo.base.addons.abirex;

import com.odoo.base.addons.abirex.dao.PosSessionDao;

public class Defaults {

    public static Integer DATA_SYNC_LIMIT = 9000;

    public static int getSyncLimit(String modelName){
        return DATA_SYNC_LIMIT;
    }

    private int journalID;

    public static boolean alwaysSyncRelations(String modelName) {
        if(modelName.equalsIgnoreCase("pos.session"))
        return true;
        else return false;
    }

    public static int getAppCompany() {
        return 3;
    }
}
