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
 * Created on 31/12/14 6:43 PM
 */
package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.Country;
import com.ehealthinformatics.data.dto.State;

public class ResCountry extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100);

    public ResCountry(Context context, OUser user) {
        super(context, "res.country", user);
    }

    public Country fromRow(final ODataRow row) {
        Country country = new Country(
                row.getInt(Columns.id),
                row.getInt(Columns.server_id),
                row.getString(Columns.Partner.name));
        return country;

    }
}
