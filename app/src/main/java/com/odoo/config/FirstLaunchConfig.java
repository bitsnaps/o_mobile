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
 * Created on 28/4/15 4:32 PM
 */
package com.odoo.config;

import android.content.Context;

import com.odoo.App;
import com.odoo.core.account.ConfigurationException;
import com.odoo.core.auth.ServerDefaultsService;
import com.odoo.core.support.OUser;

public class FirstLaunchConfig {

    public static void onFirstLaunch(Context context, OUser user)  throws ConfigurationException {

        App.initDaos(user.getUsername());
        if(!new ServerDefaultsService().updateUserConfig(context, user)){
            Throwable throwable = new Throwable();
            throwable.fillInStackTrace();
            throw new ConfigurationException("Unable to finish updating user configuration Data",  throwable);
        }

    }

}
