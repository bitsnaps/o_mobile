package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.utils.DateUtils
import com.odoo.odoorx.core.data.db.Columns
import java.util.*


class AccountBankStatementLine(var id: Int?, var serverId: Int?, var name: String?,var date: Date?,var amount: Float?, var journalCurrency: Currency?,
                           var partner: Partner?, var accountNumber: String?, var bankAccount: Account?, var account: Account?,
                               var posOrder: PosOrder?, var statement: AccountBankStatement?, var journal: AccountJournal?,
                               var ref: String?, var sequence: Int?,
                               var company: Company?, var amountCurrency: Currency?, var journalEntryName: String?
                           ) : DTO {
    constructor(posOrder: PosOrder, accountBankStatement: AccountBankStatement, amount: Float):
            this(0, 0, posOrder.name, DateUtils.now(), amount, posOrder.company?.currency, posOrder?.customer,
                     "",null, accountBankStatement.journal.debitAccount
                    ,posOrder, accountBankStatement, accountBankStatement.journal,
                    "", posOrder.sequenceNo, accountBankStatement.company, posOrder.company?.currency, "")

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        oValues.put(Columns.AccountBankStatementLine.date, date)
        oValues.put(Columns.AccountBankStatementLine.amount, amount)
        oValues.put(Columns.AccountBankStatementLine.account_number, accountNumber)
        //oValues.put(Columns.AccountBankStatementLine.journal_currency_id, journalCurrency?.id)
        oValues.put(Columns.AccountBankStatementLine.partner_id, partner?.id)
        oValues.put(Columns.AccountBankStatementLine.bank_account_id, bankAccount?.id)
        oValues.put(Columns.AccountBankStatementLine.account_id, account?.id)
        oValues.put(Columns.AccountBankStatementLine.statement_id, statement?.id)
        oValues.put(Columns.AccountBankStatementLine.ref, ref)
        oValues.put(Columns.AccountBankStatementLine.sequence, sequence)
        oValues.put(Columns.AccountBankStatementLine.company_id, company?.id)
        oValues.put(Columns.AccountBankStatementLine.pos_statement_id, posOrder?.id)
        //oValues.put(Columns.AccountBankStatementLine.currency_id, amountCurrency?.id)
        oValues.put(Columns.AccountBankStatementLine.journal_entry_name, journalEntryName)
        return oValues
    }
}