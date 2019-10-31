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
package com.odoo.base.addons.res;

import android.content.Context;

import com.odoo.App;
import com.odoo.base.addons.abirex.dto.Partner;
import com.odoo.base.addons.abirex.dto.User;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.Columns;

import java.util.ArrayList;

public class ResUsers extends OModel {
    public static final String TAG = ResUsers.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn login = new OColumn("User Login name", OVarchar.class);
    OColumn partner_id = new OColumn(null, ResPartner.class, OColumn.RelationType.ManyToOne);

    ResPartner partnerDao = null;

    @Override
     public void initDaos () {
         partnerDao = App.getDao(ResPartner.class);
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

    public User get(int id){
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow);

    }

    public User fromRow(ODataRow row) {
        ArrayList<OColumn> columns = new ArrayList<>(getRelationColumns());
        String[] columnNames = new String[columns.size()];
        for(int i = 0; i < columns.size(); i++){ columnNames[i] =  columns.get(i).getName(); }
        Integer id = row.getInt(OColumn.ROW_ID);
        Integer serverId = row.getInt(Columns.server_id);
        Partner partner = (Partner) partnerDao.fromRow(row.getM2ORecord(Columns.Partner.partner_id).browse()) ;
        String userName = row.getString(name.getName());
        String loginName = row.getString(login.getName());
        return new User(id, serverId, partner);
    }

}
