package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.StockPicking;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class StockPickingDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public StockPickingDao(Context context, OUser user) {
        super(context, "stock.picking", user);
    }

    public StockPicking get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    private StockPicking fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new StockPicking(id, name);
    }

}
