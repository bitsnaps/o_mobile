package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.Location;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;

public class LocationDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public LocationDao(Context context, OUser user) {
        super(context, ModelNames.STOCK_LOCATION, user);
    }

    public Location get(int id, QueryFields qt){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, qt);
    }

    @Override
    public Location fromRow(ODataRow row, QueryFields qt){
        Integer id = null;
        String name = null;
        if(qt.contains(Columns.id)) id = row.getInt(OColumn.ROW_ID);
        if(qt.contains(Columns.name)) name = row.getString(Columns.name);
        return new Location(id, name);
    }

}
