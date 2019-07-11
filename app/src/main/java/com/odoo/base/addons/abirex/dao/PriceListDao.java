package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.PriceList;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class PriceListDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn type_tax_use = new OColumn("TaxDao Scope", OSelection.class);
    OColumn amount_type = new OColumn("TaxDao Computation", OSelection.class)
                .addSelection("sale", "Sales")
            .addSelection("purchase", "Purchases")
            .addSelection("none", "None")
            .addSelection("adjustment", "Adjustment");
    OColumn active = new OColumn("Active", OBoolean.class);
    OColumn company_id = new OColumn(null, ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn children_tax_ids = new OColumn(null, PriceListDao.class, OColumn.RelationType.ManyToMany);

    public PriceListDao(Context context, OUser user) {
        super(context, "product.pricelist", user);
    }


    public PriceList get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    public PriceList fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new PriceList(id, name);
    }

}
