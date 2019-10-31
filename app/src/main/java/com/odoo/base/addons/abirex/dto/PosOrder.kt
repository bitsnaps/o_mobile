package com.odoo.base.addons.abirex.dto

import com.odoo.App
import com.odoo.base.addons.abirex.dao.PosOrderDao
import com.odoo.base.addons.abirex.dao.ProductDao
import com.odoo.base.addons.abirex.util.DateUtils
import com.odoo.base.addons.res.ResCompany
import com.odoo.base.addons.res.ResUsers
import com.odoo.core.orm.OValues
import com.odoo.core.support.OUser
import com.odoo.data.abirex.Columns
import java.util.*
import kotlin.collections.ArrayList

class PosOrder(var id: Int,  var serverId: Int, var name: String, var company: Company, var session: PosSession, var user: User,
               var customer: Partner, var priceList: PriceList,  var currencyRate: Float,
               var amountTax: Float, var amountTotal: Float,var amountPaid: Float, var amountReturn: Float,
               var sequenceNo: Int, var state: String, var orderDate: Date,
               var lines : ArrayList<PosOrderLine> = ArrayList()) : DTO {

    constructor(name: String, company: Company, session: PosSession, user: User, customer: Partner, priceList: PriceList, currencyRate: Float, sequenceNo: Int):
            this(0, 0, name, company, session, user, customer, priceList, currencyRate, 0F, 0F, 0F,0F,
                    sequenceNo,"draft", DateUtils.now())

    override fun toOValues(): OValues {
        val oValues = OValues()
        oValues.put(Columns.PosOrder.name, name)
        oValues.put(Columns.PosOrder.server_id, serverId)
        oValues.put(Columns.PosOrder.sequence_no, sequenceNo)
        oValues.put(Columns.PosOrder.session_id, session.id)
        oValues.put(Columns.PosOrder.company_id, company.id)
        oValues.put(Columns.PosOrder.user_id, user.id)
        oValues.put(Columns.PosOrder.partner_id, customer.id)
        oValues.put(Columns.PosOrder.currency_rate, currencyRate)
        oValues.put(Columns.PosOrder.amount_tax, amountTax)
        oValues.put(Columns.PosOrder.amount_paid, amountPaid)
        oValues.put(Columns.PosOrder.amount_total, amountTotal)
        oValues.put(Columns.PosOrder.amount_return, amountReturn)
        oValues.put(Columns.PosOrder.price_list_id, priceList.id)
        oValues.put(Columns.PosOrder.order_date, orderDate)
        oValues.put(Columns.PosOrder.state, state)
        return oValues
    }

    fun addNewOrderLine(orderLine : PosOrderLine){
        lines.add(orderLine)
    }

    public fun encode2SMS() : String{
        var SMS = "Rx:"
        lines.forEach {
            SMS = SMS.plus(it.encode2SMS() + "&")
        }
        return SMS.removeSuffix("&")
    }

}

