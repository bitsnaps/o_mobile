package com.odoo.odoorx.core.data.dao

import android.util.Log
import com.odoo.odoorx.core.base.orm.ODataRow
import com.odoo.odoorx.core.base.orm.fields.OAggregate
import com.odoo.odoorx.core.data.LazyList
import com.odoo.odoorx.core.data.dto.PurchaseOrderDate
import java.lang.reflect.Field

class PurchaseOrderDateDao(internal var purchaseOrderDao: PurchaseOrderDao, private val oDataRows: List<ODataRow>) {



    internal var group_date = OAggregate("date_order", OAggregate.Operation.DATE, "group_date")
    internal var amount_total = OAggregate("amount_total", OAggregate.Operation.SUM, "amount_total")
    internal var no_of_purchases = OAggregate("partner_id", OAggregate.Operation.COUNT)
    internal var vendor_id_count = OAggregate("partner_id", OAggregate.Operation.COUNT_DISTINCT, "vendor_id_count")


    private val purchaseOrderDateCreator: LazyList.ItemFactory<*>
        get() = object : LazyList.ItemFactory<PurchaseOrderDate> {
            override fun create(id: Int): PurchaseOrderDate {
//                val fields = ArrayList<Field>()
//                purchaseOrderDao
//
//                val groupDateIndex = cursor.getColumnIndex()
//                val amountTotalIndex = cursor.getColumnIndex(amount_total.alias)
//                val noOfPurchaseIndex = cursor.getColumnIndex(no_of_purchases.alias)
//                val vendorIdsIndex = cursor.getColumnIndex(vendor_id_count.alias)
//                try {
//
//                    val purchaseOrderDate = PurchaseOrderDate(DateUtils.parseToYYDDMM(oDataRows.getString(group_date.alias)),
//                            cursor.getInt(vendorIdsIndex), cursor.getFloat(amountTotalIndex),
//                            cursor.getInt(noOfPurchaseIndex)
//                            )
//                    return purchaseOrderDate
//                } catch (e: ParseException) {
//                    throw IllegalArgumentException(e)
//                }
                return null!!;
            }
        }


    private fun getAggregateColumn(field: Field?): OAggregate? {
        val aggregate: OAggregate
        if (field != null) {
            try {
                field.isAccessible = true
                aggregate = field.get(this) as OAggregate
                if (aggregate.name == null)
                    aggregate.name = field.name

                return aggregate
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, e.message)
            }

        }
        return null
    }


//    fun selectAllPurchaseOrderDateProxy(dateFrom: Date?, dateTo: Date?): LazyList<*> {
//        var where: String? = ""
//        val args = ArrayList<String>()
//        if (dateFrom != null && dateTo != null) {
//            where += " date_order BETWEEN  " + group_date.getoColumn() + "(?) and " + group_date.getoColumn() + "(?)"
//            args.add(DateUtils.getLowerBound(dateFrom))
//            args.add(DateUtils.getUpperBound(dateTo))
//        }
//
//        val groupBy = (if (dateTo == null) " 0==0) " else ")") + " GROUP BY ( " + group_date.alias + " "
//
//        val selection = if (where == null) groupBy else where + groupBy
//        val selectionArgs = if (args.size > 0) args.toTypedArray() else null
//
//        val fields = ArrayList<Field>()
//        fields.addAll(Arrays.asList(*javaClass.declaredFields))
//        val projection = ArrayList<String>()
//
//        for (field in fields) {
//            if (field.type.isAssignableFrom(OAggregate::class.java)) {
//                val aggregate = getAggregateColumn(field)
//                if (aggregate != null) {
//                    projection.add(aggregate.syntax)
//                }
//            }
//        }
//
//        return LazyList<PurchaseOrderDate>(purchaseOrderDateCreator,  projection.toTypedArray(), selection, selectionArgs, null, )
//    }

    companion object {

        val TAG = PurchaseOrderDateDao::class.java.simpleName
    }


}
