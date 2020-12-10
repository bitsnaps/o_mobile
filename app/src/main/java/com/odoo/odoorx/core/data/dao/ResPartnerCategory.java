package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;

public class ResPartnerCategory extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public ResPartnerCategory(Context context, OUser user) {
        super(context, "res.partner.category", user);
    }
}
