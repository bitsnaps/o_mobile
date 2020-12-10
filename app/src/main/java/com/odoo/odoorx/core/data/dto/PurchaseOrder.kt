package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.ODataRow
import com.odoo.odoorx.core.base.orm.OValues
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.List

class PurchaseOrder(var id: Int, var serverId: Int, var name: String,  var origin: String, var venderRef: String, var orderDate: Date, var approvalDate: Date,
                    var vendor: Partner, var currencyId: String, var state: String, var companyId: Int, var userId: Int, var amountUntaxed: Float,
                    var amountTax: Float, var amountTotal: Float, var lines: List<PurchaseOrderLine> = ArrayList()) : DTO {


    fun fromRow(row: ODataRow): PurchaseOrder {
        return null!!
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }


}
