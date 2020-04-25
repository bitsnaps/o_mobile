package com.ehealthinformatics.core.auth;

import android.content.Context;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.dao.AccountBankStatementDao;
import com.ehealthinformatics.data.dao.PosSessionDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dao.ResUsers;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.AccountBankStatement;
import com.ehealthinformatics.data.dto.DTO;
import com.ehealthinformatics.data.dto.PosSession;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.OValues;
import com.ehealthinformatics.core.rpc.Odoo;
import com.ehealthinformatics.core.rpc.helper.ODomain;
import com.ehealthinformatics.core.rpc.helper.OdooFields;
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooRecord;
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooResult;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.User;

import java.util.List;

public class ServerDefaultsService {

    private Context context;
    private Odoo odoo;
    private PosSessionDao posSessionDao;
    private ResUsers userDao;
    private AccountBankStatementDao accountBankStatementDao;
    OdooFields readFields = new OdooFields();

    public ServerDefaultsService(Context context, OUser oUser) {
        this.context = context;
        final App app = (App)context;
        odoo = app.getOdoo(oUser);
        posSessionDao = App.getDao(PosSessionDao.class);
        userDao = App.getDao(ResUsers.class);
        accountBankStatementDao = App.getDao(AccountBankStatementDao.class);
        readFields.addAll(new String[] {Columns.server_id, Columns.name});
    }

    public User syncUser(Integer userServerId) {
        Integer userId = userDao.selectRowId(userServerId);
        User user;
        if (userId == null || userId <= 0) {
            ODomain userDomain = new ODomain();
            userDomain.add(Columns.server_id , "=", userServerId);
             user = (User) validateResult(userDao, odoo.searchRead(ModelNames.USER, readFields, userDomain, 0, 1, "id desc"));
        } else user = userDao.get(userId, QueryFields.all());

        return user;
    }

    public List<AccountBankStatement> syncSessionStatement (PosSession posSession) {
        List<AccountBankStatement> accountBankStatements = accountBankStatementDao.posSessionStatements(posSession);
        if(accountBankStatements.isEmpty()) {
            ODomain userDomain = new ODomain();
            userDomain.add(Columns.AccountBankStatementCol.pos_session_id , "=", posSession.getServerId());
            OdooResult odooResult = odoo.searchRead(ModelNames.ACCOUNT_BANK_STATEMENT, readFields, userDomain, 0, 1, "id desc");

            ODataRow row = new ODataRow();
            OValues oValues = new OValues();
            if(odooResult.getTotalRecords() > 0){
                // OdooRecord odooRecord = odooResult.getRecords().get(0);
                for (OdooRecord odooRecord:  odooResult.getRecords()) {
                    int serverId = odooRecord.getInt(Columns.server_id);
                    row.put(Columns.server_id, serverId);
                    oValues.put(Columns.server_id, serverId);
                    ODataRow existingRow = accountBankStatementDao.browseServerId(serverId);
                    if (existingRow != null) {
                        accountBankStatements.add(accountBankStatementDao.fromRow(existingRow, QueryFields.all(), posSession));
                    } else {
                        int _id = accountBankStatementDao.insert(oValues);
                        row.put(Columns.id, _id);
                        ODataRow createdRow = accountBankStatementDao.quickCreateRecord(row);
                        Integer rowId = createdRow.getInt(Columns.id);
                        accountBankStatements.add(accountBankStatementDao.get(rowId, QueryFields.all(), posSession));
                    }
                }
            }
        }
       return accountBankStatements;
    }

    public PosSession syncCurrentOpenSession(User user){
        PosSession posSession = posSessionDao.current();
        if (posSession == null) {
            ODomain userDomain = new ODomain();
            userDomain.add("&");
            userDomain.add(Columns.PosSession.user_id, "=", user.getServerId());
            ODomain stateDomain = new ODomain();
            userDomain.add("|");
            stateDomain.add(Columns.PosSession.state , "=", Columns.PosSession.State.opened);
            stateDomain.add(Columns.PosSession.state , "=", Columns.PosSession.State.opening_control);
            userDomain.append(stateDomain);
            posSession = (PosSession) validateResult(posSessionDao, odoo.searchRead(ModelNames.POS_SESSION, null, userDomain, 0, 1, "id desc"));
        }
         return posSession;
    }

    private DTO validateResult(OModel oModel, OdooResult odooResult) {
        ODataRow row = new ODataRow();
        OValues oValues = new OValues();
        if(odooResult.getTotalRecords() > 0){
             OdooRecord odooRecord = odooResult.getRecords().get(0);
             int serverId = odooRecord.getInt(Columns.server_id);
             row.put(Columns.server_id, serverId);
             oValues.put(Columns.server_id, serverId);
             ODataRow existingRow = oModel.browseServerId(serverId);
             if (existingRow != null) {
                 return oModel.fromRow(existingRow, QueryFields.all());
             } else {
                 int _id = oModel.insert(oValues);
                 row.put(Columns.id, _id);
                 ODataRow createdRow = oModel.quickCreateRecord(row);
                 Integer rowId = createdRow.getInt(Columns.id);
                 DTO dto = oModel.get(rowId, QueryFields.all());
                 return dto;
             }
        }
        return null;
    }

}
