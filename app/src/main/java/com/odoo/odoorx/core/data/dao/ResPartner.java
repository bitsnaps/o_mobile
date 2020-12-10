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
import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.OValues;
import com.odoo.odoorx.core.base.orm.annotation.Odoo;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OBlob;
import com.odoo.odoorx.core.base.orm.fields.types.OBoolean;
import com.odoo.odoorx.core.base.orm.fields.types.ODate;
import com.odoo.odoorx.core.base.orm.fields.types.OText;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.LazyList;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.Customer;
import com.odoo.odoorx.core.data.dto.Partner;
import com.odoo.odoorx.rxshop.BuildConfig;


import java.util.ArrayList;
import java.util.List;

public class ResPartner extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".base.provider.content.sync.res_partner";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    //OColumn display_name = new OColumn("Display Name", OVarchar.class).setSize(100).setRequired();
    OColumn is_company = new OColumn("Is Company", OBoolean.class).setDefaultValue(false);
    OColumn image_small = new OColumn("Avatar", OBlob.class).setDefaultValue(false);
    OColumn street = new OColumn("Street", OVarchar.class).setSize(100);
    OColumn street2 = new OColumn("Street2", OVarchar.class).setSize(100);
    OColumn city = new OColumn("City", OVarchar.class);
    OColumn zip = new OColumn("Zip", OVarchar.class);
    OColumn website = new OColumn("Website", OVarchar.class).setSize(100);
    OColumn phone = new OColumn("Phone", OVarchar.class).setSize(15);
    OColumn mobile = new OColumn("Mobile", OVarchar.class).setSize(15);
    OColumn write_date = new OColumn("Write Date", ODate.class);
    OColumn email = new OColumn("Email", OVarchar.class);
    OColumn company_id = new OColumn("Company", ResCompany.class, OColumn.RelationType.ManyToOne);
    //   OColumn parent_id = new OColumn("Related Company", ResPartner.class, OColumn.RelationType.ManyToOne)
//            .addDomain("is_company", "=", true);

    @Odoo.Domain("[['country_id', '=', @country_id]]")
    OColumn state_id = new OColumn("State", ResCountryState.class, OColumn.RelationType.ManyToOne);
    OColumn country_id = new OColumn("Country", ResCountry.class, OColumn.RelationType.ManyToOne);
    OColumn customer = new OColumn("Customer", OBoolean.class).setDefaultValue("true");
    //OColumn employee = new OColumn("Employee", OBoolean.class).setDefaultValue("false");
    OColumn supplier = new OColumn("Supplier", OBoolean.class).setDefaultValue("false");
    OColumn comment = new OColumn("Internal Note", OText.class);
//    @Odoo.Functional(store = true, depends = {"parent_id"}, method = "storeCompanyName")
//    OColumn company_name = new OColumn("Company Name", OVarchar.class).setSize(100)
//            .setLocalColumn();
    OColumn large_image = new OColumn("Image", OBlob.class).setDefaultValue("false").setLocalColumn();

    OColumn category_id = new OColumn("Tags", ResPartnerCategory.class,
            OColumn.RelationType.ManyToMany);

//    OColumn child_ids = new OColumn("Contacts", ResPartner.class, OColumn.RelationType.OneToMany)
//            .setRelatedColumn("parent_id");

    OColumn property_account_receivable_id = new OColumn("Account Receivable", AccountDao.class, OColumn.RelationType.ManyToOne);

    public ResPartner(Context context, OUser user) {
        super(context, "res.partner", user);
        setHasMailChatter(true);
    }

    public ResCountryState stateDao;

    public void initDaos(){
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        stateDao = daoRepo.getDao(ResCountryState.class);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public String storeCompanyName(OValues value) {
        try {
            if (!value.getString("parent_id").equals("false")) {
                List<Object> parent_id = (ArrayList<Object>) value.get("parent_id");
                return parent_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public Partner fromRow(final ODataRow row, QueryFields qf) {
        if(row == null)return  null;
        Partner partner = new Partner(
                qf.contains(Columns.id) ? row.getInt(Columns.id) : null,
        qf.contains(Columns.server_id) ? row.getInt(Columns.server_id) : null,
        qf.contains(Columns.Partner.name) ? row.getString(Columns.Partner.name) : null,
        qf.contains(Columns.Partner.email) ?  row.getString(Columns.Partner.email) : null,
        qf.contains(Columns.Partner.phone) ? row.getString(Columns.Partner.phone) : null, null,
        qf.contains(Columns.Partner.address) ?  row.getString(Columns.Partner.address) : null,
        qf.contains(Columns.Partner.state_id) ?  stateDao.fromRow(row.getM2ORecord(Columns.Partner.state_id).browse()) : null,
                row.getString(Columns.Partner.locality));
        partner.setFullAddress(getAddress(row));
        partner.setFullContact(getContact(getContext(), partner.getId()));
        return  partner;
    }

    public Partner get(int id, QueryFields queryFields)
    {
        ODataRow oDataRow = browse(id);
        return fromRow(oDataRow, queryFields);
    }

    public List<Customer> searchFilter(String searchText, QueryFields queryFields){
        List<ODataRow> oDataRows = select(null, "customer = ? and name like ?", new String[]{"true", "%" + searchText + "%"});
        ArrayList<Customer> customers = new ArrayList<>();
        for(ODataRow row: oDataRows){
            customers.add(new Customer(fromRow(row, queryFields)));
        }
        return customers;
    }

    public List<Partner> selectAll(UserType type, QueryFields queryFields) {
        List<Partner>  partners
                = new ArrayList<>();
        for(ODataRow oDataRow: select()){
             partners.add( type == UserType.Customer ? new Customer(fromRow(oDataRow, queryFields)) : fromRow(oDataRow, queryFields) );
        }
        return  partners;
    }

    public LazyList<Customer> lazySelectAll(QueryFields queryFields) {
        return new LazyList<Customer>(getPartnerCreator(queryFields), select(new String[]{Columns.id}));
    }



    public static String getContact(Context context, int row_id) {
        ODataRow row = new ResPartner(context, null).browse(row_id);
        String contact;
        if (row.getString("mobile").equals("false")) {
            contact = row.getString("phone");
        } else {
            contact = row.getString("mobile");
        }
        return contact;
    }

    public String getAddress(ODataRow row) {
        String add = "";
        if (!row.getString("street").equals("false"))
            add += row.getString("street") + ", ";
        if (!row.getString("street2").equals("false"))
            add += "\n" + row.getString("street2") + ", ";
        if (!row.getString("city").equals("false"))
            add += row.getString("city");
        if (!row.getString("zip").equals("false"))
            add += " - " + row.getString("zip") + " ";
        return add;
    }


    public LazyList.ItemFactory getPartnerCreator(final QueryFields queryFields){
        return new LazyList.ItemFactory<Partner>() {
            @Override
            public Partner create(int id) {
                Partner partner = new Customer(get(id, queryFields));
                return partner;
            }
        };
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Execute upgrade script
    }
}
