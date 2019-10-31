package com.odoo.core.auth;

import android.content.Context;

import com.odoo.App;
import com.odoo.base.addons.abirex.dao.PosSessionDao;
import com.odoo.base.addons.abirex.dao.PriceListDao;
import com.odoo.base.addons.abirex.dto.Company;
import com.odoo.base.addons.abirex.dto.Currency;
import com.odoo.base.addons.abirex.dto.DTO;
import com.odoo.base.addons.abirex.dto.PosSession;
import com.odoo.base.addons.abirex.dto.PriceList;
import com.odoo.base.addons.abirex.util.DateUtils;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.base.addons.res.ResCurrency;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.rpc.Odoo;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.Columns;


public class ServerDefaultsService {

    private ResCompany companyDao;
    private PosSessionDao posSessionDao;
    private ResCurrency currencyDao;
    private PriceListDao priceListDao;

    public ServerDefaultsService() {
        companyDao = App.getDao(ResCompany.class);
        posSessionDao = App.getDao(PosSessionDao.class);
        currencyDao = App.getDao(ResCurrency.class);
        priceListDao = App.getDao(PriceListDao.class);

    }

    public boolean updateUserConfig(Context context, OUser user) {

        final App app = (App) App.getContext();
        Odoo odoo = app.getOdoo(user);

        ODomain companyDomain = new ODomain();
        companyDomain.add(Columns.server_id , "=", user.getCompanyId());

        ODomain currencyDomain = new ODomain();
        currencyDomain.add(Columns.name , "=", "NGN");

        ODomain sessionDomain = new ODomain();
        String currentSessionName = String.format("POS/%s/%02d", DateUtils.nowY(), DateUtils.nowM());
        sessionDomain.add(Columns.name , "ilike", currentSessionName);

        ODomain priceListDomain = new ODomain();
        priceListDomain.add(Columns.name , "=", "Mobile PriceList");

        OdooFields odooFields = new OdooFields();
        odooFields.addAll(new String[] {Columns.server_id});

        Company company =  (Company) validateResult(companyDao,  odoo.searchRead("res.company", odooFields, companyDomain, 0, 1, "id desc"));
        Currency currency =  (Currency) validateResult(currencyDao,  odoo.searchRead("res.currency", odooFields, currencyDomain, 0, 1, "id desc"));
        PosSession posSession = (PosSession) validateResult(posSessionDao, odoo.searchRead("pos.session", odooFields, sessionDomain, 0, 1, "id desc"));
        PriceList priceList = (PriceList) validateResult(priceListDao, odoo.searchRead("product.pricelist", odooFields, priceListDomain, 0, 1, "id desc"));

        user.setPriceListId(priceList.getServerId());
        user.setPosSessionId(posSession.getServerId());
        user.setCurrencyId(currency.getServerId());
        user.setCompanyId(company.getServerId());

        return  user.getCompanyId() > 0
                && user.getCurrencyId() > 0
                && user.getPosSessionId() > 0
                && user.getPriceListId() > 0;
    }

    public static class Defaults {

        public Defaults(Currency currency, PriceList priceList, PosSession posSession){
            this.currency = currency;
            this.priceList = priceList;
            this.posSession = posSession;
        }

        public PosSession posSession;
        public PriceList priceList;
        public Currency currency;
    }

    private DTO validateResult(OModel oModel, OdooResult odooResult) {
        ODataRow row = new ODataRow();
        OValues oValues = new OValues();
        if(odooResult.getTotalRecords() > 0){
            OdooRecord odooRecord = odooResult.getRecords().get(0);
            int serverId =  odooRecord.getInt(Columns.server_id);
            row.put(Columns.server_id, serverId);
            oValues.put(Columns.server_id, serverId);
            ODataRow existingRow = oModel.browseServerId(serverId);
            if(existingRow != null && existingRow.getInt(Columns.id) > 0){
                return oModel.fromRow(existingRow);
            }else {
                int _id =  oModel.insert(oValues);
                row.put(Columns.id, _id);
                ODataRow createdRow = oModel.quickCreateRecord(row);
                Integer rowId = createdRow.getInt(Columns.id);
                DTO dto = oModel.get(rowId);
                return dto;
            }

        }
        return null;
    }

}
