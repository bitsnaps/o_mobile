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
package com.ehealthinformatics.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.ehealthinformatics.App;
import com.ehealthinformatics.BuildConfig;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.Product;
import com.ehealthinformatics.data.dto.ProductTemplate;
import com.ehealthinformatics.core.utils.ImageUtils;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OBlob;
import com.ehealthinformatics.core.orm.fields.types.OBoolean;
import com.ehealthinformatics.core.orm.fields.types.OFloat;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.rpc.helper.ODomain;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.LazyList;
import com.ehealthinformatics.data.db.Columns;

import java.util.ArrayList;
import java.util.List;

import static com.ehealthinformatics.core.orm.fields.OColumn.*;

public class ProductDao extends OModel {

    public static final String TAG = ProductDao.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".core.provider.content.sync.product_product";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn active = new OColumn("Active", OBoolean.class).setDefaultValue(false);
    OColumn image = new OColumn("Image", OBlob.class).setDefaultValue(false);
    OColumn image_small = new OColumn("Small Image", OBlob.class).setDefaultValue(false);
    OColumn image_medium = new OColumn("Avatar", OBlob.class).setDefaultValue(false);
    OColumn lst_price = new OColumn("Sale Price", OFloat.class);
    OColumn cost_price = new OColumn("Cost Price", OFloat.class);
    OColumn qty_available = new OColumn("Quantity", OFloat.class);
    OColumn default_code = new OColumn("Default Code", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn product_tmpl_id = new OColumn(null, ProductTemplateDao.class, RelationType.ManyToOne);

    //@Odoo.Domain("[['uom_id', '=', @uom_id]]")

    private ProductTemplateDao productTemplateDao;
    private MedicineDao medicineDao;

    private List<Product> productsCache = null;
    private LazyList<Product> productsCache2 = null;

    @Override
    public ODomain defaultDomain()  {
        ODomain oDomain = super.defaultDomain();
        oDomain.add("product_tmpl_id.active", "=", "true");
        oDomain.add("product_tmpl_id.is_medicine", "!=", "true");
        oDomain.add("name", "=", "Faded SkyBlu Denim Jeans");
        return oDomain;
    }

    @Override
    public void initDaos() {
        super.initDaos();
        productTemplateDao = App.getDao(ProductTemplateDao.class);
        medicineDao = App.getDao(MedicineDao.class);
    }

    public ProductDao(Context context, OUser user) {
        super(context, ModelNames.PRODUCT, user);
        setHasMailChatter(true);
    }

    @Override
    public Product get(int id, QueryFields queryFields){
        //TODO: Remove Daos dependency, it can auto return Dto instead of ODataRow
        return fromRow(browse(id), queryFields);
    }

    public LazyList<Product> lazySelectAll(QueryFields queryFields) {
        if(productsCache2 == null) {
            productsCache2 = new LazyList<Product>(getProductCreator(queryFields), select(new String[]{Columns.id}));
        }
        return productsCache2;
    }

    public List<Product> selectAll(QueryFields queryFields) {
        if (productsCache == null){
            List<Product> products
                    = new ArrayList<>();
            for(ODataRow oDataRow: select()){
                products.add(fromRow(oDataRow, queryFields));
            }
            productsCache = products;
        }
        return productsCache;
    }

    public LazyList<Product> lazySearchFilter(String name, QueryFields queryFields){
        String[] projection = new String[]{Columns.id};
        String selection = Columns.name +  " like ?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        String sortOrder = Columns.name + " ASC  LIMIT 10";
        return new LazyList<Product>(getProductCreator(queryFields), select(projection, selection, selectionArgs, sortOrder));
    }

    public List<Product> searchFilter(String name, QueryFields queryFields){
        String[] projection = null;
        String selection = Columns.name +  " like ?";
        String[] selectionArgs = new String[]{"%" + name + "%"};
        String sortOrder = Columns.name + " ASC  LIMIT 10";
        List<ODataRow> oDataRows = select(projection, selection, selectionArgs, sortOrder);
        List<Product> products = new ArrayList<>();
        for(ODataRow oDataRow: oDataRows){
            products.add(fromRow(oDataRow, queryFields));
        }
        return products;
    }

    public LazyList.ItemFactory getProductCreator(final QueryFields queryFields){
        return new LazyList.ItemFactory<Product>() {
            @Override
            public Product create(int id) {
               Product product = get(id,  queryFields);
               return product;
            }
        };
    }

    public Product fromRow(ODataRow row, QueryFields qf){
        Integer id = null, serverId = null;
        Float price = null, qtyAvailable = null, cost = null;
        String name = null, defaultCode = null;
        Boolean active = false;
        Bitmap image = null, imageSmall  = null, imageMedium = null;
        ProductTemplate productTemplate = null;
        if(qf.contains(Columns.id)) id = row.getInt(Columns.id);
        if(qf.contains(Columns.server_id)) serverId = row.getInt(Columns.server_id);
        if(qf.contains(Columns.name)) name = row.getString(Columns.name);
        if(qf.contains(Columns.active)) active = Boolean.valueOf(row.getString(Columns.ProductCol.active));
        if(qf.contains(Columns.ProductCol.image)) image = ImageUtils.getBitmapFromString(App.getContext(), row.getString(Columns.ProductCol.image));
        if(qf.contains(Columns.ProductCol.image_small)) imageSmall = ImageUtils.getBitmapFromString(App.getContext(), row.getString(Columns.ProductCol.image_small));
        if(qf.contains(Columns.ProductCol.image_medium)) imageMedium = ImageUtils.getBitmapFromString(App.getContext(), row.getString(Columns.ProductCol.image_medium));
        if(qf.contains(Columns.ProductCol.lst_price)) price = row.getFloat(Columns.ProductCol.lst_price);
        if(qf.contains(Columns.ProductCol.cost_price)) cost = row.getFloat(Columns.ProductCol.cost_price);
        if(qf.contains(Columns.ProductCol.qty_available)) qtyAvailable = row.getFloat(Columns.ProductCol.qty_available);
        if(qf.contains(Columns.ProductCol.default_code)) defaultCode = row.getString(Columns.ProductCol.default_code);
        if(qf.contains(Columns.ProductCol.product_tmpl_id))  productTemplate = productTemplateDao.fromRow(row.getM2ORecord(Columns.ProductCol.product_tmpl_id).browse(), qf.childField(Columns.ProductCol.product_tmpl_id));
        String code = row.getString(Columns.ProductCol.code);
        Product product = new Product(id, serverId, name,"", active,
                productTemplate, imageSmall, imageMedium, image,
                price, qtyAvailable,
                defaultCode, code, cost);
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
