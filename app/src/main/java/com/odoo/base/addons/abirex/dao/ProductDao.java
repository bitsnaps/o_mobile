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
package com.odoo.base.addons.abirex.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.util.Log;

import com.odoo.App;
import com.odoo.BuildConfig;
import com.odoo.base.addons.abirex.Utils;
import com.odoo.base.addons.abirex.model.Product;
import com.odoo.base.addons.abirex.model.ProductTemplate;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.data.DataLoader;
import com.odoo.data.LazyList;

import static com.odoo.core.orm.fields.OColumn.*;

public class ProductDao extends OModel {

    public static final String TAG = ProductDao.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".core.provider.content.sync.product_product";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn active = new OColumn("Active", OBoolean.class).setDefaultValue(false);
    OColumn image = new OColumn("Image", OBlob.class).setDefaultValue(false);
    OColumn image_small = new OColumn("Avatar", OBlob.class).setDefaultValue(false);
    OColumn lst_price = new OColumn("Sale Price", OFloat.class);
    OColumn qty_available = new OColumn("Quantity", OFloat.class);
    OColumn default_code = new OColumn("Default Code", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn product_tmpl_id = new OColumn(null, ProductTemplateDao.class, RelationType.ManyToOne);
            //.addDomain("active","=",true);



    //@Odoo.Domain("[['uom_id', '=', @uom_id]]")
    OColumn uom_id = new OColumn("UOM", UoMDao.class, OColumn.RelationType.ManyToOne);

    public ProductDao(Context context, OUser user) {
        super(context, "product.product", user);
        setHasMailChatter(true);
    }

    public Product get(int id){
        ODataRow oDataRow = browse(id);
        String name = oDataRow.getString("name");
        Boolean active = oDataRow.getBoolean("active");
        Bitmap imageSmall = BitmapUtils.getBitmapImageOrFalse(getContext(), oDataRow.getString("image_small"));
        Bitmap image = BitmapUtils.getBitmapImageOrFalse(getContext(), oDataRow.getString("image"));
        Float lastPrice = oDataRow.getFloat("lst_price");
        Float qtyAvailable = oDataRow.getFloat("qty_available");
        String defaultCode = oDataRow.getString("default_code");
        String code = oDataRow.getString("code");
        ProductTemplate productTemplate = ProductTemplateDao.fromRow(oDataRow.getM2ORecord("product_tmpl_id").browse());
        return new Product(id, name, active, productTemplate, image, imageSmall, lastPrice, qtyAvailable, defaultCode, code);
    }

    public Loader<LazyList<Product>> selectAll() {
        return new DataLoader(getContext(), uri(), null, null, null, null, getProductCreator());
    }

    private static LazyList.ItemFactory getProductCreator(){
        return new LazyList.ItemFactory<Product>() {
            @Override
            public Product create(Cursor cursor, int index) {
                cursor.moveToPosition(index);
               ODataRow row = OCursorUtils.toDatarow(cursor );
               Product product = fromRow(row);
               return product;

            }
        };
    }

    public static Product fromRow(ODataRow row){

        Integer id = row.getInt("id");
        String name = row.getString("name");
        boolean active = Boolean.valueOf(row.getString("active"));
        Bitmap image = Utils.getBitmapFromString(App.getContext(), row.getString("image"));
        Bitmap imageSmall = Utils.getBitmapFromString(App.getContext(), row.getString("image_small"));
        Float price = row.getFloat("lst_price");
        Float qtyAvailable = row.getFloat("qty_available");
        String defaultCode = row.getString("default_code");
        ProductTemplate productTemplate = ProductTemplateDao.fromRow(row.getM2ORecord("product_tmpl_id").browse());
        String code = row.getString("code");
        Product product = new Product(id, name, active,
                productTemplate, image, imageSmall,
                price, qtyAvailable,
                defaultCode, code);
        return  product;
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
        Log.e(TAG, "ProductDao->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "ProductDao->onSyncFinished");
    }
}
