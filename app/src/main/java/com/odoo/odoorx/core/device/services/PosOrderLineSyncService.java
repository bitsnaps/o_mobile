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
 * Created on 2/1/15 11:07 AM
 */
package com.odoo.odoorx.core.device.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.odoorx.core.base.service.OSyncAdapter;
import com.odoo.odoorx.core.base.service.OSyncService;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.dao.PosOrderLineDao;

public class PosOrderLineSyncService extends OSyncService {
    public static final String TAG = PosOrderLineSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, PosOrderLineDao.class, this, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        //adapter.syncDataLimit(Defaults.DATA_SYNC_LIMIT);
    }


}
