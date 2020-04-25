package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.db.Columns.AccountBankStatementCol
import java.util.*
import kotlin.collections.ArrayList

class AccountBankStatement(var id: Int, var serverId: Int?, var name: String, var reference: String, var posSession: PosSession?, var account: Account?, var date: Date, var dateDone: Date?,
                           var balanceStart: Float, var balanceEnd: Float, var accountingDate: Date?, var state: AccountBankStatementCol.State,
                           var journal: AccountJournal, var company: Company, var totalEntryEnc: Float, var isDifferenceZero: Boolean,
                           var allLinesReconciled: Boolean, var statements: ArrayList<AccountBankStatementLine>, var user: User,
                           var startCashBox: AccountBankStatementCashBox?, var endCashBox: AccountBankStatementCashBox? ) : DTO {

    constructor(posSession: PosSession):
            this(0, 0, posSession.name!!, "",
            posSession,
            posSession.config?.journal?.debitAccount,
            DateUtils.now(),null,
            0.0F,
            0.0F,
            null,
                    AccountBankStatementCol.State.opened,
            posSession?.config?.journal!!,
            posSession?.config?.company!!, 0.0F, false, false,
                    ArrayList<AccountBankStatementLine>(),
            posSession.user!!,
                    null,
                    null)

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(AccountBankStatementCol.name, name)
        oValues.put(AccountBankStatementCol.reference, reference)
        oValues.put(AccountBankStatementCol.user_id, user.id)
        oValues.put(AccountBankStatementCol.pos_session_id, posSession!!.id)
        oValues.put(AccountBankStatementCol.account_id, account?.id)
        oValues.put(AccountBankStatementCol.date, DateUtils.parseToDB(date))
        oValues.put(AccountBankStatementCol.balance_start, balanceStart)
        oValues.put(AccountBankStatementCol.balance_end, balanceEnd)
        oValues.put(AccountBankStatementCol.accounting_date, accountingDate)
        oValues.put(AccountBankStatementCol.state, state.value)
        oValues.put(AccountBankStatementCol.all_lines_reconciled, allLinesReconciled)
        oValues.put(AccountBankStatementCol.company_id, company.id)
        oValues.put(AccountBankStatementCol.total_entry_encoding, totalEntryEnc)
        oValues.put(AccountBankStatementCol.is_difference_zero, isDifferenceZero)
        oValues.put(AccountBankStatementCol.cashbox_start_id, startCashBox?.id)
        oValues.put(AccountBankStatementCol.cashbox_end_id, endCashBox?.id)
        oValues.put(AccountBankStatementCol.journal_id, journal.id)
        return oValues
    }
}