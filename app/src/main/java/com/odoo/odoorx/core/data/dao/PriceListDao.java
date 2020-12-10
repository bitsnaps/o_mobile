package com.odoo.odoorx.core.data.dao;

import android.content.Context;


import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OBoolean;
import com.odoo.odoorx.core.base.orm.fields.types.OSelection;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Currency;
import com.odoo.odoorx.core.data.dto.PriceList;

public class PriceListDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn type_tax_use = new OColumn("TaxDao Scope", OSelection.class);
    OColumn amount_type = new OColumn("TaxDao Computation", OSelection.class)
                .addSelection("sale", "Sales")
            .addSelection("purchase", "Purchases")
            .addSelection("none", "None")
            .addSelection("adjustment", "Adjustment");
    OColumn active = new OColumn("Active", OBoolean.class);
    //OColumn company_id = new OColumn(null, ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn currency_id = new OColumn(null, ResCurrency.class, OColumn.RelationType.ManyToOne);

    ResCurrency currencyDao;

    public void initDaos() {
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        currencyDao = daoRepo.getDao(ResCurrency.class);
    }
    public PriceListDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT_PRICELIST, user);
    }

    public PriceList get(int id, QueryFields qt){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, qt);
    }

    public PriceList fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        String name  = null;
        Currency currency = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        if(qf.contains(Columns.PriceList.currency_id)) currency = currencyDao.fromRow(row.getM2ORecord(Columns.PriceList.currency_id).browse(), qf.childField(Columns.PriceList.currency_id));
        return new PriceList(id, serverId, name, currency);
    }

}
