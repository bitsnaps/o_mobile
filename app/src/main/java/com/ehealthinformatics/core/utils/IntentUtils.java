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
 * Created on 18/12/14 11:31 AM
 */
package com.ehealthinformatics.core.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ehealthinformatics.data.db.Columns;

public class IntentUtils {

    public static void openURLInBrowser(Context context, String url) {
        if (!url.equals("false") && !url.equals("")) {
            if (!url.contains("http")) {
                url = "http://" + url;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    public static void startActivity(Context context, Class<?> activity_class, Bundle data) {
       startActivity(context, activity_class, data, true);
    }

    public static void startActivity(Context context, Class<?> activity_class, Bundle data, boolean clearBatchStack) {
        Intent intent = new Intent(context, activity_class);
        if (data != null)
            intent.putExtras(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(clearBatchStack)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void redirectToMap(Context context, String location) {
        if (!location.equals("false") && !location.equals("")) {
            String map = "geo:0,0?q=" + location;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            context.startActivity(intent);
        }
    }

    public static void requestMessage(Context context, String email) {
        if (!email.equals("false") && !email.equals("")) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.setData(Uri.parse("mailto:" + email));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void requestCall(Context context, String number) {
        if (!number.equals("false") && !number.equals("")) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
        }
    }

    public static class IntentParams {
        public static String ID = Columns.id;
        public static String EDIT_MODE = "edit_mode";
        public static String POS_ORDER_ID = "pos_order_id";
    }
}
