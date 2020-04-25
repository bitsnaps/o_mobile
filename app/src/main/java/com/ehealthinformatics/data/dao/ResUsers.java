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
 * Created on 13/1/15 10:16 AM
 */
package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.dto.Company;
import com.ehealthinformatics.data.dto.Partner;
import com.ehealthinformatics.data.dto.User;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;

import java.util.ArrayList;

public class ResUsers extends OModel {
    public static final String TAG = ResUsers.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn login = new OColumn("User Login name", OVarchar.class);
    OColumn partner_id = new OColumn(null, ResPartner.class, OColumn.RelationType.ManyToOne);
    OColumn company_id = new OColumn(null, ResCompany.class, OColumn.RelationType.ManyToOne);

    ResPartner partnerDao = null;
    ResCompany companyDao = null;

    @Override
     public void initDaos () {
         partnerDao = App.getDao(ResPartner.class);
         companyDao = App.getDao(ResCompany.class);
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

    public ResUsers(Context context, OUser user) {
        super(context, "res.users", user);
    }

    public static int myId(Context context) {
        ResUsers users = new ResUsers(context, null);
        return users.selectRowId(users.getUser().getUserId());
    }

    public User get(int id, QueryFields queryFields){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields);

    }

    public User fromRow(ODataRow row, QueryFields qf) {
//        ArrayList<OColumn> columns = new ArrayList<>(getRelationColumns());
//        String[] columnNames = new String[columns.size()];
//        for(int i = 0; i < columns.size(); i++){ columnNames[i] =  columns.get(i).getName(); }
        Integer id = null, serverId = null;
        Partner partner = null;
        Company company = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.Partner.partner_id)) partner = partnerDao.fromRow(row.getM2ORecord(Columns.Partner.partner_id).browse(), qf.childField(Columns.Partner.partner_id)) ;
        if(qf.contains(Columns.Partner.company_id))  company = companyDao.fromRow(row.getM2ORecord(Columns.Partner.company_id).browse(), qf.childField(Columns.Partner.company_id)) ;
        return new User(id, serverId, partner, company);
    }

}
