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
 * Created on 8/1/15 5:47 PM
 */
package com.ehealthinformatics.app.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import com.ehealthinformatics.app.activity.product.ProductEdit;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.rpc.helper.ODomain;
import com.ehealthinformatics.core.utils.IntentUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ehealthinformatics.App;
import com.ehealthinformatics.R;
import com.ehealthinformatics.app.activity.utils.ShareUtil;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.feature.OFileManager;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.OValues;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.rpc.helper.OdooFields;
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooResult;
import com.ehealthinformatics.core.support.OdooCompatActivity;
import com.ehealthinformatics.core.utils.BitmapUtils;
import com.ehealthinformatics.core.utils.OAlert;
import com.ehealthinformatics.core.utils.OResource;
import com.ehealthinformatics.core.utils.OStringColorUtil;
import com.ehealthinformatics.data.db.Columns;

import odoo.controls.OField;
import odoo.controls.OForm;

public class ProductDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {
    public static final String TAG = ProductDetails.class.getSimpleName();
    public static String KEY_PARTNER_TYPE = "partner_type";
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private ProductDao productDao;
    private ODataRow record = null;
    private ImageView productImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        collapsingToolbarLayout = findViewById(R.id.product_collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        productImage = findViewById(R.id.iv_product_image);
        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();
        productDao = App.getDao(ProductDao.class);
        extras = getIntent().getExtras();
        // if (hasRecordInExtra())
        //    partnerType = ProductCategoryList.Type.valueOf(extras.getString(KEY_PARTNER_TYPE));
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();

        findViewById(R.id.btn_product_tmpl_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putInt(Columns.id, record.getInt(Columns.ProductCol.product_tmpl_id));
                IntentUtils.startActivity(getApplicationContext(), ProductEdit.class, data);
            }
        });
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(IntentUtils.IntentParams.ID);
    }

    private void setMode(Boolean edit) {
        findViewById(R.id.captureImage).setVisibility(edit ? View.VISIBLE : View.GONE);
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_product_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_product_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_product_save).setVisible(edit);
            mMenu.findItem(R.id.menu_product_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString(Columns.name));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.productFormEdit);
            findViewById(R.id.product_view_layout).setVisibility(View.GONE);
            findViewById(R.id.product_edit_layout).setVisibility(View.VISIBLE);
            OField is_company = (OField) findViewById(R.id.is_company_edit);
            //is_company.setOnValueChangeListener(this);
        } else {
            mForm = (OForm) findViewById(R.id.productForm);
            findViewById(R.id.product_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.product_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            productImage.setColorFilter(Color.parseColor("#ffffff"));
            productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(IntentUtils.IntentParams.ID);
            record = productDao.browse(rowId);
            // record.put("full_address", productDao.getAddress(record));
            // checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString(Columns.name));
            setProductImage();
            if (record.getInt(Columns.id) != 0 && record.getString("large_image").equals("false")) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.full_address:
                IntentUtils.redirectToMap(this, record.getString("full_address"));
                break;
            case R.id.website:
                IntentUtils.openURLInBrowser(this, record.getString("website"));
                break;
            case R.id.email:
                IntentUtils.requestMessage(this, record.getString("email"));
                break;
            case R.id.phone_number:
                IntentUtils.requestCall(this, record.getString("phone"));
                break;
            case R.id.mobile_number:
                IntentUtils.requestCall(this, record.getString("mobile"));
                break;
            case R.id.captureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
            case R.id.btn_product_tmpl_edit:
                Bundle data = new Bundle();
                data.putInt(Columns.id, record.getInt(Columns.ProductCol.product_tmpl_id));
                IntentUtils.startActivity(this, ProductEdit.class, data);
                break;
        }
    }

    private void checkControls() {
        findViewById(R.id.full_address).setOnClickListener(this);
        findViewById(R.id.website).setOnClickListener(this);
        findViewById(R.id.email).setOnClickListener(this);
        findViewById(R.id.phone_number).setOnClickListener(this);
        findViewById(R.id.mobile_number).setOnClickListener(this);
    }

    private void setProductImage() {
        String image = record.getString("image");
        String image_medium = record.getString("image_medium");
        if (record != null && !image_medium.equals("false")) {
            productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String base64 = newImage;
            if (newImage == null) {
                if (!image.equals("false") && !image.isEmpty()) {
                    base64 = record.getString("image");
                } else {
                    base64 = record.getString("image_medium");
                }
            }
            productImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
            productImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            productImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString(Columns.name));
            productImage.setBackgroundColor(color);
        }
    }

    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_product_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (newImage != null) {
                        Bitmap bitmap = BitmapUtils.getBitmapImage(this, newImage);
                        Bitmap bitmapSmall = BitmapUtils.resizeImage(72, 72, bitmap);
                        Bitmap bitmapMedium = BitmapUtils.resizeImage(144, 144, bitmap);
                        Bitmap bitmapLarge = BitmapUtils.resizeImage(480, 800, bitmap);
                        values.put("image_small", BitmapUtils.toBase64(bitmapSmall));
                        values.put("image_medium",  BitmapUtils.toBase64(bitmapMedium));
                        values.put("image",  BitmapUtils.toBase64(bitmapLarge));
                    }
                    if (record != null) {
                        productDao.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = productDao.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_product_cancel:
            case R.id.menu_product_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    setProductImage();
                } else {
                    finish();
                }
                break;
            case R.id.menu_product_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_product_sync:
                final ProgressDialog dialog = ProgressDialog.show(this, "",
                        "Loading. Please wait...", true);
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... strings) {
                        ODomain oDomain = new ODomain();
                        oDomain.add(Columns.server_id,  "=", productDao.selectServerId(Integer.parseInt(strings[0])));
                        productDao.quickSyncRecords(oDomain);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dialog.dismiss();
                    }
                }.execute(extras.getInt(Columns.id) +"");

                break;
            case R.id.menu_product_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_product_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (productDao.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(ProductDetails.this, R.string.toast_record_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.of_edt_product_tmpl_id).setVisibility(view);
        }
    }

    private class BigImageLoader extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            String image = null;
            try {
                Thread.sleep(300);
                OdooFields fields = new OdooFields();
                fields.addAll(new String[]{"image_medium"});
                OdooResult record = productDao.getServerDataHelper().read(null, params[0]);
                if (record != null && !record.getString("image_medium").equals("false")) {
                    image = record.getString("image_medium");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (!result.equals("false")) {
                    OValues values = new OValues();
                    values.put("image", result);
                    productDao.update(record.getInt(OColumn.ROW_ID), values);
                    record.put("image", result);
                    setProductImage();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
        outState.putString(KEY_NEW_IMAGE, newImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
            productImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            productImage.setColorFilter(null);
            productImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }
}