package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.Location;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class LocationDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);


    public LocationDao(Context context, OUser user) {
        super(context, "stock.location", user);
    }


    public Location get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    private Location fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new Location(id, name);
    }

}
