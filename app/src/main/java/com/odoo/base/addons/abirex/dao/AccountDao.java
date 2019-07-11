package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.Account;
import com.odoo.base.addons.abirex.model.Location;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class AccountDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);

    public AccountDao(Context context, OUser user) {
        super(context, "account.account", user);
    }

    public Account get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    private Account fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new Account(id, name);
    }

}
