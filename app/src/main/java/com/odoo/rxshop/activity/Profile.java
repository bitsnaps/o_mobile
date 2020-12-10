/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 9/1/15 11:54 AM
 */
package com.odoo.rxshop.activity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.odoo.RxShop;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.odoo.odoorx.rxshop.R;
import com.odoo.odoorx.core.data.dao.PosSessionDao;
import com.odoo.odoorx.core.data.dao.PriceListDao;
import com.odoo.odoorx.core.data.dao.ResCompany;
import com.odoo.odoorx.core.data.dao.ResCurrency;
import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.base.utils.BitmapUtils;
import com.odoo.odoorx.core.base.utils.OAppBarUtils;
import com.odoo.odoorx.core.base.utils.OStringColorUtil;
import com.odoo.odoorx.core.data.dto.PosSession;

import odoo.controls.OForm;

public class Profile extends AppCompatActivity {
    public static final String TAG = Profile.class.getSimpleName();
    ResCompany companyDao = RxShop.getDao(ResCompany.class);
    PriceListDao priceListDao = RxShop.getDao(PriceListDao.class);
    PosSessionDao posSessionDao = RxShop.getDao(PosSessionDao.class);
    ResCurrency currencyDao = RxShop.getDao(ResCurrency.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_profile);
        OAppBarUtils.setAppBar(this, R.id.toolbar, true);
        OUser user = OUser.current(this);
        OForm form = (OForm) findViewById(R.id.profileDetails);
        int color = OStringColorUtil.getStringColor(this, user.getName());
        form.setIconTintColor(color);

        ODataRow userData = new ODataRow();
        userData.put("name", user.getName());
        userData.put("user_login", user.getUsername());
        userData.put("server_url", user.getHost());
        userData.put("database", user.getDatabase());
        userData.put("version", user.getOdooVersion().getServerSerie());
        userData.put("timezone", user.getTimezone());
        PosSession posSession = posSessionDao.get(posSessionDao.selectRowId(user.getPosSessionId()), QueryFields.all());
        userData.put("company", companyDao.get(companyDao.selectRowId(user.getCompanyId()), QueryFields.idName()).getName());
        userData.put("price_list", posSession.getConfig().getPriceList().getName());
        userData.put("pos_session", posSession.getName());
        userData.put("currency", posSession.getConfig().getPriceList().getCurrency().getName() + " (" + posSession.getConfig().getPriceList().getCurrency().getSymbol() + ")");
        form.initForm(userData);

        CollapsingToolbarLayout collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setTitle(userData.getString("name"));
        setTitle(userData.getString("name"));
        Bitmap avatar;
        if (user.getAvatar().equals("false")) {
            avatar = BitmapUtils.getAlphabetImage(this, user.getName());
        } else {
            avatar = BitmapUtils.getBitmapImage(this, user.getAvatar());
        }
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(avatar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

