/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 30/12/14 3:11 PM
 */
package com.odoo.odoorx.rxshop.config;

import com.odoo.rxshop.fragment.CustomerList2;
import com.odoo.rxshop.fragment.CustomerWelcomePage;
import com.odoo.rxshop.fragment.DashboardStatistics;
import com.odoo.rxshop.fragment.PosOrderList;
import com.odoo.rxshop.fragment.ProductList;
import com.odoo.rxshop.fragment.SyncModelList;
import com.odoo.odoorx.rxshop.base.support.addons.AddonsHelper;
import com.odoo.odoorx.rxshop.base.support.addons.OAddon;

public class Addons extends AddonsHelper {

    /**
     * Declare your required module here
     * NOTE: For maintain sequence use object name in asc order.
     * Ex.:
     * OAddon partners = new OAddon(Partners.class).setDefault();
     * for maintain sequence call withSequence(int sequence)
     * OAddon partners = new OAddon(Partners.class).withSequence(2);
     */

    OAddon dashboard =   new OAddon(DashboardStatistics.class).setDefault();
    OAddon customers = new OAddon(CustomerList2.class);
    OAddon pointOfSale = new OAddon(PosOrderList.class);
    OAddon syncModel =   new OAddon(SyncModelList.class);
//    OAddon purchase = new OAddon(PurchaseList.class);
    OAddon products = new OAddon(ProductList.class);
    OAddon customerWelcome = new OAddon(CustomerWelcomePage.class);
//    OAddon productCategory = new OAddon(ProductCategoryList.class);
}
