package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.Account;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;

public class AccountDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public AccountDao(Context context, OUser user) {
        super(context, "account.account", user);
    }

    public Account get(int id, QueryFields queryFields){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, QueryFields.id());
    }

    public Account fromRow(ODataRow row, QueryFields queryFields){
        if(row  == null) return null;
        Integer id = row.getInt(Columns.id);
        String name = row.getString(Columns.name);
        return new Account(id, name);
    }

}
