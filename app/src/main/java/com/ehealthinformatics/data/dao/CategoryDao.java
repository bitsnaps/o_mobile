package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.Category;

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
