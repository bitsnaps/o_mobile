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
package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.base.addons.abirex.model.Currency;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class CurrencyDao extends OModel {
    public static final String TAG = CurrencyDao.class.getSimpleName();
    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn symbol = new OColumn("Symbol", OVarchar.class).setSize(10);

    public CurrencyDao(Context context, OUser user) {
        super(context, "res.currency", user);
    }

    public static String getSymbol(Context context, int row_id) {
        CurrencyDao resCurrency = new CurrencyDao(context, null);
        ODataRow row = resCurrency.browse(row_id);
        return (row != null) ? row.getString("symbol") : "";
    }

    public Currency get(int id){
        ODataRow oDataRow = browse(id);
        return fromDataRow(oDataRow);
    }

    public Currency fromDataRow(ODataRow row){
        Integer id = row.getInt(OColumn.ROW_ID);
        String name = row.getString(this.name.getName());
        String symbol = row.getString(this.symbol.getName());
        return new Currency(id, name, symbol);
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

}
