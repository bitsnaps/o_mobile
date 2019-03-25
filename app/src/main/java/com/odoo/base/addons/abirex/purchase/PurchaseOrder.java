package com.odoo.base.addons.abirex.purchase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.base.addons.res.ResCurrency;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import static com.odoo.core.orm.fields.OColumn.RelationType;

public class PurchaseOrder extends OModel {

    public static final String TAG = PurchaseOrder.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".core.provider.content.sync.purchase_order";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn origin = new OColumn("Origin", OVarchar.class);
    OColumn partner_ref = new OColumn("Vendor Reference", OVarchar.class);
    OColumn date_order = new OColumn("Order Date", ODateTime.class);
    OColumn date_approve = new OColumn("Date Approved", ODateTime.class);
    OColumn partner_id = new OColumn("Partner Id", ResPartner.class, RelationType.ManyToOne);
    OColumn currency_id = new OColumn("Currency", ResCurrency.class, RelationType.ManyToOne);
    OColumn state = new OColumn("Vendor Reference", OSelection.class)
            .addSelection("draft", "RFQ")
            .addSelection("sent", "RFQ Sent")
            .addSelection("to approve", "To Approve")
            .addSelection("purchase", "Purchase Order")
            .addSelection("done", "Locked")
            .addSelection("cancel", "Cancelled");
    OColumn company_id = new OColumn("Company", ResCompany.class, RelationType.ManyToOne);
    OColumn user_id = new OColumn(null, ResUsers.class, RelationType.ManyToOne);

    OColumn amount_untaxed = new OColumn("Untaxed Amount", OFloat.class);
    OColumn amount_tax = new OColumn("Taxes", OFloat.class);
    OColumn amount_total = new OColumn("Total Amount", OFloat.class);

//
//    @Odoo.Domain("[['uom_id", "=', @uom_id]]")
//    OColumn uom_id = new OColumn("UOM", UoM.class, OColumn.RelationType.ManyToOne);

    public PurchaseOrder(Context context, OUser user) {
        super(context, "purchase.order", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {

        return buildURI(AUTHORITY);
    }
//
//    public String storeCompanyName(OValues value) {
//        try {
//            if (!value.getString("parent_id").equals("false")) {
//                List<Object> parent_id = (ArrayList<Object>) value.get("parent_id");
//                return parent_id.get(1) + "";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    public static String getContact(Context context, int row_id) {
//        ODataRow row = new ProductProduct(context, null).browse(row_id);
//        String contact;
//        if (row.getString("mobile").equals("false")) {
//            contact = row.getString("phone");
//        } else {
//            contact = row.getString("mobile");
//        }
//        return contact;
//    }
//
//    public String getAddress(ODataRow row) {
//        String add = "";
//        if (!row.getString("street").equals("false"))
//            add += row.getString("street") + ", ";
//        if (!row.getString("street2").equals("false"))
//            add += "\n" + row.getString("street2") + ", ";
//        if (!row.getString("city").equals("false"))
//            add += row.getString("city");
//        if (!row.getString("zip").equals("false"))
//            add += " - " + row.getString("zip") + " ";
//        return add;
//    }


    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onSyncStarted(){
        Log.e(TAG, "PurchaseOrder->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "PurchaseOrder->onSyncFinished");
    }
}
