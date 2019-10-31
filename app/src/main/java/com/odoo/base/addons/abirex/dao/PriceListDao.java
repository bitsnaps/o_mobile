package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.dto.PriceList;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.Columns;
import com.odoo.data.abirex.ModelNames;

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

    public PriceListDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT_PRICELIST, user);
    }

    public PriceList get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);
    }

    public PriceList fromRow(ODataRow row){
        Integer id = row.getInt(Columns.id);
        Integer serverId = row.getInt(Columns.server_id);
        String name = row.getString(this.name.getName());
        return new PriceList(id, serverId, name);
    }

}
