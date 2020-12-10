package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.StockPickingType;

public class StockPickingTypeDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public StockPickingTypeDao(Context context, OUser user) {
        super(context, ModelNames.STOCK_PICKING_TYPE, user);
    }

    public StockPickingType get(int id, QueryFields queryFields){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields);

    }

    @Override
    public StockPickingType fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        String name = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        return new StockPickingType(id, name);
    }

}
