package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;

public class UoMCategoryDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn description = new OColumn("Description", OVarchar.class);
    OColumn measure_type = new OColumn("Measure Type", OVarchar.class);

    public UoMCategoryDao(Context context, OUser user) {
        super(context, "uom.category", user);
    }
}
