package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.dto.StockPickingType;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.ModelNames;

public class StockPickingTypeDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public StockPickingTypeDao(Context context, OUser user) {
        super(context, ModelNames.STOCK_PICKING_TYPE, user);
    }

    public StockPickingType get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    @Override
    public StockPickingType fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new StockPickingType(id, name);
    }

}
