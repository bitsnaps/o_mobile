package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.data.db.Columns

class AccountJournal(var id: Int,var serverId: Int, var name: String, var debitAccount: Account, var creditAccount: Account) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.server_id, serverId)
        oValues.put(Columns.AccountJournal.default_credit_account_id, creditAccount.id)
        oValues.put(Columns.AccountJournal.default_debit_account_id, debitAccount.id)
        return oValues
    }
}