/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 *
 * Created on 31/12/14 12:53 PM
 */
package com.odoo.odoorx.core.data.dao;

import android.content.Context;


import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.Company;
import com.odoo.odoorx.core.data.dto.Currency;

public class ResCompany extends OModel {

    ResCurrency currencyDao = null;
    public static final String TAG = ResCompany.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn currency_id = new OColumn("Currency", ResCurrency.class,
            OColumn.RelationType.ManyToOne);
    //OColumn partner_id = new OColumn(null, ResPartner.class, OColumn.RelationType.ManyToOne);

    public ResCompany(Context context, OUser user) {
        super(context, "res.company", user);
    }

    @Override
     public void initDaos() {
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
         currencyDao = daoRepo.getDao(ResCurrency.class);
     }

    public Company get(int id, QueryFields qf) {
        ODataRow oDataRow =   browse(id);
        return fromRow(oDataRow, qf);
    }

    public Company fromRow(ODataRow row, QueryFields qf) {
        if(row  == null) return null;
        Integer id = null, serverId = null;
        String name = null;
        Currency currency = null;
        id = qf.contains(Columns.id) ? row.getInt(Columns.id) : null;
        serverId = qf.contains(Columns.server_id) ? row.getInt(Columns.server_id) : null;
        currency = qf.contains(Columns.Company.currency_id) ? currencyDao.get(row.getInt(Columns.Company.currency_id), qf.childField(Columns.Company.currency_id)) : null;
        name = row.getString(this.name.getName());
        return new Company(id, serverId, name, currency);
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
        ResCompany company = new ResCompany(context, null);
        return company.selectRowId(company.getUser().getCompanyId());
    }

    public static int myCurrency(Context context) {
        ResCompany company = new ResCompany(context, null);
        ODataRow row = company.browse(company.selectRowId(company.
                getUser().getCompanyId()));
        return row.getM2ORecord("currency_id").browse().getInt(OColumn.ROW_ID);
    }
}
