package com.ehealthinformatics.data.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.ehealthinformatics.BuildConfig;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.PurchaseOrder;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.ODateTime;
import com.ehealthinformatics.core.orm.fields.types.OFloat;
import com.ehealthinformatics.core.orm.fields.types.OSelection;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.LazyList;

import static com.ehealthinformatics.core.orm.fields.OColumn.RelationType;

public class PurchaseOrderDao extends OModel {

    public static final String TAG = PurchaseOrderDao.class.getSimpleName();
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
            .addSelection("purchase", "Purchase List Order")
            .addSelection("done", "Locked")
            .addSelection("cancel", "Cancelled");
    OColumn company_id = new OColumn("Company", ResCompany.class, RelationType.ManyToOne);
    OColumn user_id = new OColumn(null, ResUsers.class, RelationType.ManyToOne);

    OColumn amount_untaxed = new OColumn("Untaxed Amount", OFloat.class);
    OColumn amount_tax = new OColumn("Taxes", OFloat.class);
    OColumn amount_total = new OColumn("Total Amount", OFloat.class);

    public PurchaseOrderDao(Context context, OUser user) {
        super(context, ModelNames.PURCHASE_ORDER, user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {

        return buildURI(AUTHORITY);
    }

    public LazyList<PurchaseOrder> selectAllPurchaseOrder(QueryFields queryFields) {
        return new LazyList<>(getPurchaseOrderCreator(queryFields), select(new String[]{Columns.id}));

    }

    private LazyList.ItemFactory getPurchaseOrderCreator(final QueryFields queryFields){
       return new LazyList.ItemFactory<PurchaseOrder>() {
            @Override
            public PurchaseOrder create(int id) {
                return (PurchaseOrder) get(id, queryFields);
            }
        };
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onSyncStarted(){
        Log.e(TAG, "PurchaseOrderDao->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "PurchaseOrderDao->onSyncFinished");
    }
}
