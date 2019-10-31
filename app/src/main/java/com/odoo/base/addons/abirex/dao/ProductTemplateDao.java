package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.dto.ProductTemplate;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.Columns;
import com.odoo.data.abirex.ModelNames;

import static com.odoo.data.abirex.Columns.ProductTemplateCol;

public class ProductTemplateDao extends OModel {

    OColumn name = new OColumn(Columns.name, OVarchar.class);
    OColumn description = new OColumn( Columns.description, OVarchar.class);
    OColumn active = new OColumn(Columns.active, OBoolean.class);

    public ProductTemplateDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT_TEMPLATE, user);
    }

    public ProductTemplate get(int id){
        return fromRow(browse(id));
    }

    public ProductTemplate fromRow(ODataRow row){
        if(row == null)return new ProductTemplate(0, "Not Available", true, "");
        int id = row.getInt(Columns.id);
        String name = row.getString(Columns.name);
        Boolean active = row.getBoolean(Columns.active);
        String productType = row.getString(ProductTemplateCol.productType);
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
