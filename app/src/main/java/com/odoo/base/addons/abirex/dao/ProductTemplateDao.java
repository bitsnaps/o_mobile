package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.ProductTemplate;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ProductTemplateDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn description = new OColumn("Code", OVarchar.class);
    OColumn active = new OColumn("Active", OBoolean.class);

    public ProductTemplateDao(Context context, OUser user) {
        super(context, "product.template", user);
    }

    public ProductTemplate get(int id){
        return fromRow(browse(id));
    }

    public static ProductTemplate fromRow(ODataRow row){
        int id = row.getInt("id");
        String name = row.getString("name");
        Boolean active = row.getBoolean("active");
        String productType = row.getString("product_type");

        ProductTemplate productTemplate = new ProductTemplate(id, name, active,productType);
        return  productTemplate;
    }

    @Override
    public boolean allowCreateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowUpdateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowDeleteRecordInLocal() {
        return false;
    }

}
