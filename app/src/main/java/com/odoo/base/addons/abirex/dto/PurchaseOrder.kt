package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OValues

import java.util.Date

class PurchaseOrder : DTO {

    var name: String? = null
    var origin: String? = null
    var venderRef: String? = null
    var orderDate: Date? = null
    var approvalDate: Date? = null
    var vendorId: Int? = null
    var currencyId: String? = null
    var state: String? = null

    var companyId: Int? = null
    var userId: Int? = null

    var amountUntaxed: Float? = null
    var amountTax: Float? = null
    var amountTotal: Float? = null

    constructor()

    fun fromRow(row: ODataRow): PurchaseOrder {
        return PurchaseOrder()
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }


}
