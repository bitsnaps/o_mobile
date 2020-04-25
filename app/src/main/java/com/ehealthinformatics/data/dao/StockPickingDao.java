package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.StockPicking;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;

public class StockPickingDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public StockPickingDao(Context context, OUser user) {
        super(context, ModelNames.STOCK_PICKING, user);
    }

    public StockPicking get(int id, QueryFields qf){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, qf);

    }

    @Override
    public StockPicking fromRow(ODataRow row, QueryFields qf) {
        Integer id = null, serverId = null;
        String name = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        return new StockPicking(id, name);
    }

}
