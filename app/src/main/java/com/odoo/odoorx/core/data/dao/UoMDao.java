package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OBoolean;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Uom;

import java.util.ArrayList;
import java.util.List;

public class UoMDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn category_id = new OColumn("UoMDao Category", UoMCategoryDao.class, OColumn.RelationType.ManyToOne);
    OColumn active = new OColumn("Active", OBoolean.class);

    public UoMDao(Context context, OUser user) {
        super(context, ModelNames.UOM, user);
    }

    public Uom fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        String name = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        Uom uom = new Uom(id, name);
        return  uom;
    }

    public List<Uom> getUOMList() {
        List<ODataRow> rows = select();
        ArrayList<Uom> uoms = new ArrayList();
        int index = rows.size();
        while(index-- > 0) {
            uoms.add(fromRow(rows.get(index), QueryFields.all()));
        }
        return uoms;
    }
}
