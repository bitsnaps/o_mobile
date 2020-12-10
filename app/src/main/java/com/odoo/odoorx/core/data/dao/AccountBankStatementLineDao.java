package com.odoo.odoorx.core.data.dao;

import android.content.Context;


import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.ODate;
import com.odoo.odoorx.core.base.orm.fields.types.OFloat;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.base.utils.DateUtils;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.Account;
import com.odoo.odoorx.core.data.dto.AccountBankStatement;
import com.odoo.odoorx.core.data.dto.AccountBankStatementLine;
import com.odoo.odoorx.core.data.dto.AccountJournal;
import com.odoo.odoorx.core.data.dto.Company;
import com.odoo.odoorx.core.data.dto.Currency;
import com.odoo.odoorx.core.data.dto.Partner;
import com.odoo.odoorx.core.data.dto.PosOrder;
import com.odoo.odoorx.rxshop.BuildConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.odoo.odoorx.core.base.orm.fields.OColumn.RelationType.ManyToOne;

public class AccountBankStatementLineDao extends OModel {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".base.provider.content.sync.account_bank_statement_line";

    OColumn name = new OColumn ("Name", OVarchar.class);
    OColumn date = new OColumn ("Date", ODate.class);
    OColumn amount = new OColumn ("Difference", OFloat.class);
    OColumn journal_currency_id = new OColumn (null, ResCurrency.class, ManyToOne);
    OColumn partner_id = new OColumn (null, ResPartner.class, ManyToOne);
    OColumn account_number = new OColumn ("Bank Acct no", OVarchar.class);
    OColumn bank_account_id = new OColumn (null, AccountDao.class, ManyToOne);
    OColumn account_id = new OColumn (null, AccountDao.class, ManyToOne);
    OColumn statement_id =  new OColumn (null, AccountBankStatementDao.class, ManyToOne);
    OColumn journal_id =  new OColumn (null, AccountJournalDao.class, ManyToOne);
    OColumn ref = new OColumn ("Ref", OVarchar.class);
    OColumn note = new OColumn ("Note", OVarchar.class);

    OColumn sequence = new OColumn ("Sequence", OVarchar.class);
    OColumn company_id = new OColumn ("Company", OVarchar.class);
    //OColumn journal_entry_ids = new OColumn ("Note", AccountMoveLineDao.class, OneToMany);
    OColumn amount_currency = new OColumn ("Amount in Currency", OFloat.class);
    OColumn currency_id =  new OColumn (null, ResCurrency.class, ManyToOne);
    OColumn move_name = new OColumn ("Move name", OVarchar.class);
    //TODO: Don't pull more than this
    OColumn pos_statement_id =  new OColumn (null, PosOrderDao.class, ManyToOne);

    ResCurrency currencyDao;
    ResPartner partnerDao;
    AccountDao accountDao;
    AccountBankStatementDao accountBankStatementDao;
    AccountJournalDao accountJournalDao;
    ResCompany companyDao;
    PosOrderDao posOrderDao;
    public AccountBankStatementLineDao(Context context, OUser user) {
        super(context, "account.bank.statement.line", user);
    }

    public void initDaos(){
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        companyDao  = daoRepo.getDao(ResCompany.class);
        currencyDao  = daoRepo.getDao(ResCurrency.class);
        partnerDao  = daoRepo.getDao(ResPartner.class);
        accountDao  = daoRepo.getDao(AccountDao.class);
        accountBankStatementDao  = daoRepo.getDao(AccountBankStatementDao.class);
        accountJournalDao  = daoRepo.getDao(AccountJournalDao.class);
        posOrderDao  = daoRepo.getDao(PosOrderDao.class);
    }

    public AccountBankStatementLine get(int id, QueryFields queryFields, PosOrder posOrder) {
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields, posOrder);
    }

    public List<AccountBankStatementLine> forStatement(PosOrder posOrder, AccountBankStatement  accountBankStatement, QueryFields qf) {
        List<ODataRow> oDataRows = select(null, Columns.AccountBankStatementLine.statement_id  + " = ? and " + Columns.AccountBankStatementLine.pos_statement_id + " = ?", new String[]{ accountBankStatement.getId() + "", posOrder.getId() + ""});
        ArrayList<AccountBankStatementLine> lines =  new ArrayList<>();
        for(ODataRow row: oDataRows){
            lines.add(fromRow(row, qf, posOrder));
        }
        return lines;
    }

    public AccountBankStatementLine fromRow(ODataRow row, QueryFields queryFields, PosOrder posOrder) {
        Integer id = queryFields.contains(Columns.AccountBankStatementLine.id) ? row.getInt(Columns.AccountBankStatementLine.id) : null;
        Integer serverId = queryFields.contains(Columns.AccountBankStatementLine.server_id) ? row.getInt(Columns.AccountBankStatementLine.server_id) : null;
        String name = queryFields.contains(Columns.AccountBankStatementLine.name) ? row.getString(Columns.AccountBankStatementLine.name) : null;
        Date date = queryFields.contains(Columns.AccountBankStatementLine.date) ? DateUtils.parseFromDB(row.getString(Columns.AccountBankStatementLine.date)) : null;
        String ref = queryFields.contains(Columns.AccountBankStatementLine.ref) ? row.getString(Columns.AccountBankStatementLine.ref) : null;
        String note = queryFields.contains(Columns.AccountBankStatementLine.note) ? row.getString(Columns.AccountBankStatementLine.note) : null;
        Integer sequence = queryFields.contains(Columns.AccountBankStatementLine.sequence) ? row.getInt(Columns.AccountBankStatementLine.sequence) : null;
        Float amountCurrency = queryFields.contains(Columns.AccountBankStatementLine.amount_currency) ? row.getFloat(Columns.AccountBankStatementLine.amount_currency) : null;
        Float amount = queryFields.contains(Columns.AccountBankStatementLine.amount) ? row.getFloat(Columns.AccountBankStatementLine.amount) : null;
        String accountNumber = queryFields.contains(Columns.AccountBankStatementLine.account_number) ? row.getString(Columns.AccountBankStatementLine.account_number) : null;
        Account bankAccount = queryFields.contains(Columns.AccountBankStatementLine.bank_account_id) ? accountDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.bank_account_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.bank_account_id)) : null;
        Account account = queryFields.contains(Columns.AccountBankStatementLine.account_id) ? accountDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.account_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.account_id)) : null;
        Partner partner = queryFields.contains(Columns.AccountBankStatementLine.partner_id) ? partnerDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.partner_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.partner_id)) : null;
        AccountBankStatement accountBankStatement = queryFields.contains(Columns.AccountBankStatementLine.statement_id) ? accountBankStatementDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.statement_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.statement_id), null) : null;
        AccountJournal accountJournal = queryFields.contains(Columns.AccountBankStatementLine.journal_id) ? accountJournalDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.journal_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.journal_id)) : null;
        posOrder = posOrder != null ? posOrder : queryFields.contains(Columns.AccountBankStatementLine.pos_statement_id) ?  posOrderDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.pos_statement_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.pos_statement_id)) : null;
        Company company =  queryFields.contains(Columns.AccountBankStatementLine.company_id) ?
              companyDao.get(Integer.parseInt(row.getString(Columns.AccountBankStatementLine.company_id)),  queryFields.childField(Columns.AccountBankStatementLine.company_id)) : null;
        Currency currency = queryFields.contains(Columns.AccountBankStatementLine.currency_id) ? currencyDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.currency_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.currency_id)) : null;
        Currency journalCurrency = queryFields.contains(Columns.AccountBankStatementLine.journal_currency_id) ? currencyDao.fromRow(row.getM2ORecord(Columns.AccountBankStatementLine.journal_currency_id).browse(), queryFields.childField(Columns.AccountBankStatementLine.journal_currency_id)) : null;
        return new AccountBankStatementLine(id, serverId, name, date, amount, journalCurrency, partner, accountNumber, bankAccount, account, posOrder, accountBankStatement, accountJournal,
                ref, sequence, company,currency, "");
    }

}
