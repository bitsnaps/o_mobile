package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public CategoryDao(Context context, OUser user) {
        super(context, ModelNames.CATEGORY, user);
    }

    public Category fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        String name = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        Category category = new Category(id, name);
        return  category;
    }

    public List getCategoryList() {
        List<ODataRow> rows = select();
        ArrayList<Category> categories = new ArrayList();
        int index = rows.size();
        while(index-- > 0) {
            categories.add(fromRow(rows.get(index), QueryFields.all()));
        }
        return categories;
    }
}
