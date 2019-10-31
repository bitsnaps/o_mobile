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
import com.odoo.base.addons.abirex.dto.Product;
import com.odoo.base.addons.abirex.dto.ProductTemplate;
import com.odoo.base.addons.abirex.util.ImageUtils;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.data.DataLoader;
import com.odoo.data.LazyList;
import com.odoo.data.abirex.Columns;
import com.odoo.data.abirex.ModelNames;

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

    //@Odoo.Domain("[['uom_id', '=', @uom_id]]")
    OColumn uom_id = new OColumn("UOM", UoMDao.class, OColumn.RelationType.ManyToOne);

    private ProductTemplateDao productTemplateDao;

    public ODomain defaultDomain()  {
        ODomain oDomain = super.defaultDomain();
        oDomain.add("product_tmpl_id.active", "=", "true");
        return oDomain;
    }


    @Override
    public void initDaos() {
        super.initDaos();
        productTemplateDao = App.getDao(ProductTemplateDao.class);
    }

    public ProductDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT, user);
        setHasMailChatter(true);
    }

    @Override
    public Product get(int id){
        ODataRow oDataRow = browse(id);
        int serverId = oDataRow.getInt("id");
        String name = oDataRow.getString("name");
        Boolean active = oDataRow.getBoolean("active");
        Bitmap imageSmall = BitmapUtils.getBitmapImageOrFalse(getContext(), oDataRow.getString("image_small"));
        Bitmap image = BitmapUtils.getBitmapImageOrFalse(getContext(), oDataRow.getString("image"));
        Float lastPrice = oDataRow.getFloat("lst_price");
        Float qtyAvailable = oDataRow.getFloat("qty_available");
        String defaultCode = oDataRow.getString("default_code");
        String code = oDataRow.getString("code");
        //TODO: Reomve Daos dependency, it can auto return Dto instead of ODataRow
        ProductTemplate productTemplate = productTemplateDao.fromRow(oDataRow.getM2ORecord("product_tmpl_id").browse());
        return new Product(id, serverId, name, active, productTemplate, image, imageSmall, lastPrice, qtyAvailable, defaultCode, code);
    }

    public Loader<LazyList<Product>> selectAll() {
        return new DataLoader(getContext(), uri(), null, null, null, null, getProductCreator(projection(), this));
    }

    public Loader<LazyList<Product>> searchByName(String name){
        String[] projection = null;
        String selection = Columns.name +  " like ?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        String sortOrder = Columns.name + " ASC  LIMIT 5";
        return new DataLoader(getContext(), uri(), projection, selection, selectionArgs, sortOrder, getProductCreator(projection(), this));
    }

    private static LazyList.ItemFactory getProductCreator(final String[] projections, final ProductDao productDao){
        return new LazyList.ItemFactory<Product>() {
            @Override
            public Product create(Cursor cursor, int index) {
                cursor.moveToPosition(index);
               ODataRow row = OCursorUtils.toDatarow(cursor );
               productDao.populateRelatedColumns(projections, row);
               Product product = productDao.fromRow(row);
               return product;

            }
        };
    }

    public Product fromRow(ODataRow row){
        Integer id = row.getInt("_id");
        Integer serverId = row.getInt("id");
        String name = row.getString("name");
        boolean active = Boolean.valueOf(row.getString("active"));
        Bitmap image = ImageUtils.getBitmapFromString(App.getContext(), row.getString("image"));
        Bitmap imageSmall = ImageUtils.getBitmapFromString(App.getContext(), row.getString("image_small"));
        Float price = row.getFloat("lst_price");
        Float qtyAvailable = row.getFloat("qty_available");
        String defaultCode = row.getString("default_code");
        ProductTemplate productTemplate = productTemplateDao.fromRow(row.getM2ORecord("product_tmpl_id").browse());
        String code = row.getString("code");
        Product product = new Product(id,serverId, name, active,
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
