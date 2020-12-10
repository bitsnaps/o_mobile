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
 * Created on 30/12/14 4:00 PM
 */
package com.odoo.odoorx.core.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

;
import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.base.utils.BitmapUtils;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.db.ModelNames;
import com.odoo.odoorx.core.data.dto.ProductImage;
import com.odoo.odoorx.rxshop.BuildConfig;

import java.util.ArrayList;
import java.util.List;

public class ProductImageDao extends OModel {

    public static final String TAG = ProductDao.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".base.provider.content.sync.product_image";

    OColumn image = new OColumn("Image", OVarchar.class).setSize(100).setRequired();
    OColumn product_tmpl_id = new OColumn("Product", ProductTemplateDao.class, OColumn.RelationType.ManyToOne).setRequired();

    @Override
    public void initDaos() {
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        super.initDaos();
    }

    public ProductImageDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT_IMAGE, user);
        setHasMailChatter(false);
    }

    public ProductImage fromRow(ODataRow row, QueryFields queryFields){
        Integer id = null, serverId = null;
        String image = null;
        if(queryFields.contains(Columns.id)) id = row.getInt(Columns.id);
        if(queryFields.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(queryFields.contains(Columns.ProductImageCol.image)) image = row.getString(Columns.ProductImageCol.image);
        ProductImage productImage = new ProductImage(id, serverId, BitmapUtils.getBitmapImage(getContext(), image));
        return  productImage;
    }

    public List<ProductImage> getImages(int productTemplateId) {
        String[] projection = null;
        String selection = Columns.ProductImageCol.product_tmpl_id +  " = ?";
        String[] selectionArgs = new String[]{productTemplateId+""};
        String sortOrder = Columns.id + " ASC  LIMIT 10";
        List<ODataRow> oDataRows = select(projection, selection, selectionArgs, sortOrder);
        ArrayList<ProductImage> productImages = new ArrayList<>();
        for (int i = 0; i < oDataRows.size(); i++) {
            productImages.add(fromRow(oDataRows.get(i), QueryFields.all()));
        }
        return productImages;
    }

    @Override
    public ProductImage get(int id, QueryFields queryFields){
        return fromRow(browse(id), queryFields);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onSyncStarted(){
        Log.e(TAG, "ProductImageDao->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "ProductImageDao->onSyncFinished");
    }
}
