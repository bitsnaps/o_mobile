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
 * Created on 13/1/15 10:19 AM
 */
package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.data.dto.Currency;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OFloat;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.User;

import java.util.ArrayList;

public class ResCurrency extends OModel {
    public static final String TAG = ResCurrency.class.getSimpleName();
    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn symbol = new OColumn("Symbol", OVarchar.class).setSize(10);
    OColumn rate = new OColumn("Rate", OFloat.class);

    public ResCurrency(Context context, OUser user) {
        super(context, "res.currency", user);
    }

    public static String getSymbol(Context context, int row_id) {
        ResCurrency resCurrency = new ResCurrency(context, null);
        ODataRow row = resCurrency.browse(row_id);
        return (row != null) ? row.getString("symbol") : "";
    }

    public Currency get(int id, QueryFields queryFields) {
        ODataRow oDataRow = browse(id);
        ArrayList<OColumn> columns = new ArrayList<>(getRelationColumns());
        String[] columnNames = new String[columns.size()];
        for(int i = 0; i < columns.size(); i++){ columnNames[i] =  columns.get(i).getName(); }
        populateRelatedColumns(columnNames, oDataRow);
        return fromRow(oDataRow,queryFields);
    }

    public Currency fromRow(ODataRow row, QueryFields  qf) {
        if(row  == null) return null;
        ArrayList<OColumn> columns = new ArrayList<>(getRelationColumns());
        String[] columnNames = new String[columns.size()];
        for(int i = 0; i < columns.size(); i++){ columnNames[i] =  columns.get(i).getName(); }
        int id = row.getInt(Columns.id);
        int serverId = row.getInt(Columns.server_id);
        String name = row.getString(Columns.name);
        String symbol = row.getString(Columns.Currency.symbol);
        Float rate = row.getFloat(Columns.Currency.rate);
        return new Currency(id, serverId, name, symbol, rate);
    }

}
