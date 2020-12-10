package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OBoolean;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.dto.AccountBankStatementCashBox;

import static com.odoo.odoorx.core.base.orm.fields.OColumn.RelationType.ManyToOne;

public class AccountBankStatementCashBoxDao extends OModel {

    OColumn name = new OColumn ("Name", OVarchar.class);
    OColumn user_id = new OColumn (null, ResUsers.class, ManyToOne);
    OColumn is_difference_zero =  new OColumn ("Difference", OBoolean.class);


    public AccountBankStatementCashBoxDao(Context context, OUser user) {
        super(context, "account.bank.statement.cashbox", user);
    }

    public AccountBankStatementCashBox fromRow(ODataRow browse, QueryFields childField) {
        return null;
    }
}
