package com.odoo.base.addons.abirex.dao;


import android.content.Context;

import com.odoo.base.addons.abirex.model.Company;
import com.odoo.base.addons.res.ResCurrency;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class CompanyDao extends OModel {

    public static final String TAG = com.odoo.base.addons.res.ResCompany.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn currency_id = new OColumn("Currency", ResCurrency.class,
            OColumn.RelationType.ManyToOne);

    public CompanyDao(Context context, OUser user) {
        super(context, "res.company", user);
    }

    public static ODataRow getCurrency(Context context) {
        com.odoo.base.addons.res.ResCompany company = new com.odoo.base.addons.res.ResCompany(context, null);
        int row_id = company.selectRowId(company.getUser().getCompanyId());
        return company.browse(row_id).getM2ORecord("currency_id").browse();
    }

    public Company get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    public Company fromRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        return new Company(id, name);
    }

    @Override
    public boolean allowCreateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowUpdateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowDeleteRecordInLocal() {
        return false;
    }

    public static int myId(Context context) {
        com.odoo.base.addons.res.ResCompany company = new com.odoo.base.addons.res.ResCompany(context, null);
        return company.selectRowId(company.getUser().getCompanyId());
    }

    public static int myCurrency(Context context) {
        com.odoo.base.addons.res.ResCompany company = new com.odoo.base.addons.res.ResCompany(context, null);
        ODataRow row = company.browse(company.selectRowId(company.
                getUser().getCompanyId()));
        return row.getM2ORecord("currency_id").browse().getInt(OColumn.ROW_ID);
    }
}