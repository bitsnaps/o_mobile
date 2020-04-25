package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.ODateTime;
import com.ehealthinformatics.core.orm.fields.types.OFloat;
import com.ehealthinformatics.core.orm.fields.types.OInteger;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.PurchaseOrder;
import com.ehealthinformatics.data.dto.PurchaseOrderLine;

import java.util.List;

public class PurchaseOrderLineDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn sequence = new OColumn("Sequence", OInteger.class);
    OColumn product_qty = new OColumn("Quantity", OFloat.class);
    OColumn product_uom_qty = new OColumn("Total Quantity", OFloat.class);
    OColumn taxes_id = new OColumn(null, TaxDao.class, OColumn.RelationType.ManyToMany);
    OColumn product_id = new OColumn("Product", ProductDao.class, OColumn.RelationType.ManyToOne);
    OColumn price_subtotal = new OColumn("Subtotal", OFloat.class);
    OColumn price_total = new OColumn("Total", OFloat.class);
    OColumn price_tax = new OColumn("TaxDao", OFloat.class);

    OColumn order_id = new OColumn("Order", PurchaseOrderDao.class, OColumn.RelationType.ManyToOne);


    OColumn date_planned = new OColumn("Planned Date", ODateTime.class);

    public PurchaseOrderLineDao(Context context, OUser user) {
        super(context, ModelNames.PURCHASE_ORDER_LINE, user);
    }

    public List<PurchaseOrderLine> fromPurchaseOrder(PurchaseOrder purchaseOrder){
        return null;
    }
}
