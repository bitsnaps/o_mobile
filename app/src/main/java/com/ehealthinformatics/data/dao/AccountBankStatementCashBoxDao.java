package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.data.dto.AccountBankStatementCashBox;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OBoolean;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;

import static com.ehealthinformatics.core.orm.fields.OColumn.RelationType.ManyToOne;

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
