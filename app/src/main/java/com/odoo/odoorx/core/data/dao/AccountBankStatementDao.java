package com.odoo.odoorx.core.data.dao;

import android.content.Context;

import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OBoolean;
import com.odoo.odoorx.core.base.orm.fields.types.ODate;
import com.odoo.odoorx.core.base.orm.fields.types.OFloat;
import com.odoo.odoorx.core.base.orm.fields.types.OInteger;
import com.odoo.odoorx.core.base.orm.fields.types.OSelection;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.base.utils.DateUtils;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.Columns.AccountBankStatementCol.State;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.Account;
import com.odoo.odoorx.core.data.dto.AccountBankStatement;
import com.odoo.odoorx.core.data.dto.AccountBankStatementCashBox;
import com.odoo.odoorx.core.data.dto.AccountBankStatementLine;
import com.odoo.odoorx.core.data.dto.AccountJournal;
import com.odoo.odoorx.core.data.dto.Company;
import com.odoo.odoorx.core.data.dto.PosSession;
import com.odoo.odoorx.core.data.dto.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.odoo.odoorx.core.base.orm.fields.OColumn.RelationType.ManyToOne;
import static com.odoo.odoorx.core.base.orm.fields.OColumn.RelationType.OneToMany;

public class AccountBankStatementDao extends OModel {

    Columns.AccountBankStatementCol c = new Columns.AccountBankStatementCol();
    OColumn name = new OColumn ("Name", OVarchar.class);
    OColumn pos_session_id = new OColumn ("Pos Session", PosSessionDao.class, ManyToOne);
    OColumn account_id = new OColumn (null, AccountDao.class, ManyToOne);
    OColumn reference = new OColumn ("Reference", OVarchar.class);
    OColumn date = new OColumn ("Date", ODate.class);
    OColumn date_done = new OColumn ("Date Done", OVarchar.class);
    OColumn balance_start = new OColumn ("Start Balance", OVarchar.class);
    OColumn balance_end_real = new OColumn ("Name", OFloat.class);
    OColumn accounting_date = new OColumn ("Acct Date", ODate.class);
    OColumn state = new OColumn ("State", OSelection.class)
            .addSelection("open", "New")
            .addSelection("confirm", "Validated");
    OColumn journal_id =  new OColumn (null, AccountJournalDao.class, ManyToOne);
    OColumn company_id = new OColumn (null, ResCompany.class, ManyToOne);
    OColumn total_entry_encoding = new OColumn ("Total", OFloat.class);
    OColumn balance_end = new OColumn ("End Balance", OFloat.class);
    OColumn difference = new OColumn ("Difference", OFloat.class);
    OColumn line_ids = new OColumn (null, AccountBankStatementLineDao.class, OneToMany);
    OColumn move_line_ids = new OColumn (null, AccountMoveLineDao.class, OneToMany);
    OColumn move_line_count =  new OColumn ("Move Line", OInteger.class);
    OColumn all_lines_reconciled = new OColumn ("All Lines Reconciled", OBoolean.class);
    OColumn user_id = new OColumn (null, ResUsers.class, ManyToOne);
    OColumn cashbox_start_id = new OColumn  (null, AccountBankStatementCashBoxDao.class, ManyToOne);
    OColumn cashbox_end_id = new OColumn  (null, AccountBankStatementCashBoxDao.class, ManyToOne);
    OColumn is_difference_zero =  new OColumn ("Is Difference", OBoolean.class);

    private  PosSessionDao posSessionDao;
    private  AccountDao accountDao;
    private  ResUsers userDao;
    private ResCurrency currencyDao;
    private ResCompany companyDao;
    private AccountJournalDao journalDao;
    private  AccountBankStatementCashBoxDao accountBankStatementCashBoxDao;

    @Override
    public void initDaos() {
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        posSessionDao = daoRepo.getDao(PosSessionDao.class);
        accountDao = daoRepo.getDao(AccountDao.class);
        userDao = daoRepo.getDao(ResUsers.class);
        currencyDao = daoRepo.getDao(ResCurrency.class);
        companyDao = daoRepo.getDao(ResCompany.class);
        journalDao = daoRepo.getDao(AccountJournalDao.class);
        accountBankStatementCashBoxDao = daoRepo.getDao(AccountBankStatementCashBoxDao.class);
    }

    public AccountBankStatementDao(Context context, OUser user) {
        super(context, ModelNames.ACCOUNT_BANK_STATEMENT, user);
    }

    public AccountBankStatement get(int id, QueryFields queryFields, PosSession posSession) {
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields, posSession);
    }

    public List<AccountBankStatement> posSessionStatements(PosSession posSession) {
        List<AccountBankStatement> posSessionStatements = new ArrayList<>();
        List<ODataRow> oDataRows = select(null, "pos_session_id = ?", new String[]{posSession.getId() + ""});
        if (oDataRows.size() > 0) {
            for(ODataRow oDataRow : oDataRows) {
                posSessionStatements.add(fromRow(oDataRow, QueryFields.all(), posSession));
            }
        } else {
            AccountBankStatement accountBankStatement = new AccountBankStatement(posSession);
            int id =  insert(accountBankStatement.toOValues());
            accountBankStatement.setId(id);
            posSessionStatements.add(accountBankStatement);
        }
        return posSessionStatements;
    }

    public AccountBankStatement fromRow(ODataRow row, QueryFields queryFields, PosSession posSession) {
        if(row  == null) return null;
        Integer id = queryFields.contains(c.id) ? row.getInt(c.id) : null;
        Integer serverId = queryFields.contains(c.server_id) ? row.getInt(c.server_id) : null;
        Float totalEntryEnc = queryFields.contains(c.total_entry_encoding) ? row.getFloat(c.total_entry_encoding) : null;
        Float balanceStart = queryFields.contains(c.balance_start) ? row.getFloat(c.balance_start) : null;
        Float balanceEnd = queryFields.contains(c.balance_end) ? row.getFloat(c.balance_end) : null;
        Date date = queryFields.contains(c.date) ? DateUtils.parseFromDB(row.getString(c.date)) : null;
        Date accountingDate = queryFields.contains(c.accounting_date) ? DateUtils.parseFromDB(row.getString(c.accounting_date)) : null;
        Date dateDone = queryFields.contains(c.date_done) ? DateUtils.parseFromDB(row.getString(c.date_done)) : null;
        String name = queryFields.contains(c.name) ? row.getString(c.name)  : null;
        String reference = queryFields.contains(c.reference) ? row.getString(c.reference) : null;
        Account account = queryFields.contains(c.account_id) ? accountDao.fromRow(row.getM2ORecord(c.account_id).browse(), queryFields.childField(c.account_id)) : null;
        Company company = queryFields.contains(c.company_id) ? company = companyDao.fromRow(row.getM2ORecord(c.company_id).browse(), queryFields.childField(c.company_id)) : null;
        Boolean allLinesReconciled = queryFields.contains(c.all_lines_reconciled) ? row.getBoolean(c.all_lines_reconciled) : null;
        Boolean isDifferenceZero = queryFields.contains(c.is_difference_zero) ? row.getBoolean(c.is_difference_zero) : null;
        State state = queryFields.contains(c.state) ? State.valueOfString(row.getString(c.state)) : null;
        User user = queryFields.contains(c.user_id) ? userDao.fromRow(row.getM2ORecord(c.user_id).browse(), queryFields.childField(c.user_id)) : null;
        AccountJournal accountJournal = queryFields.contains(c.journal_id) ? journalDao.fromRow(row.getM2ORecord(c.journal_id).browse(), queryFields.childField(c.journal_id)) : null;
        AccountBankStatementCashBox startCashBox = queryFields.contains(c.cashbox_start_id) ? accountBankStatementCashBoxDao.fromRow(row.getM2ORecord(c.cashbox_start_id).browse(), queryFields.childField(c.cashbox_start_id)) : null;
        AccountBankStatementCashBox endCashBox = queryFields.contains(c.cashbox_end_id) ? accountBankStatementCashBoxDao.fromRow(row.getM2ORecord(c.cashbox_end_id).browse(), queryFields.childField(c.cashbox_end_id)) : null;
        return new AccountBankStatement(id, serverId, name, reference, posSession, account, date, dateDone, balanceStart, balanceEnd, accountingDate, state, accountJournal,
                 company, totalEntryEnc, isDifferenceZero, allLinesReconciled, new ArrayList<AccountBankStatementLine>(), user,
                startCashBox, endCashBox);
    }

}
