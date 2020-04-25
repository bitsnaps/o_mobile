package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.db.Columns
import java.util.*

class AccountBankStatementCashBox(var id: Int, var name: String, var posSession: PosSession, var account: Account, var date: Date, var dateDone: Date,
                                  var balanceStart: Float, var balanceEnd: Float, var accountingDate: Date, var state: String, var currency: Currency,
                                  var journal: AccountJournal, var journalType: String, var company: Company, var totalEntryEnc: Float,
                                  var statements: List<AccountBankStatementLine>, var user: User) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        oValues.put(Columns.name, name)
        return oValues
    }
}