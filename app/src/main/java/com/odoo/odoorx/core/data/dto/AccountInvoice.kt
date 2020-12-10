package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.utils.DateUtils
import com.odoo.odoorx.core.data.db.Columns
import java.util.*
import kotlin.collections.ArrayList

class AccountInvoice(var id: Int?, var serverId: Int?, var name: String?,  var origin: String?, var type: String?,
                     var reference: String?, var number: String?,
                     var customer: Customer?, var company: Company?, var user: User?, var account: Account?,
                     var currency: Currency?, var sent: Boolean?,
                     var amountTax: Float?, var amountTotal: Float?, var amountPaid: Float?, var amountReturn: Float?,
                     var state: String?, var invoiceDate: Date?, var dueDate: Date?,
                     var lines : ArrayList<AccountInvoiceLine> = ArrayList()) : DTO {

//    constructor(name: String, company: Company, session: PosSession, user: User, customer: Customer, priceList: PriceList, currencyRate: Float, sequenceNo: Int):
//            this(0, 0, name, company, session, user, customer, priceList, currencyRate, 0F, 0F, 0F,0F,
//                    sequenceNo,"draft", DateUtils.now())

    override fun toOValues(): OValues {
        val oValues = OValues()
        oValues.put(Columns.AccountInvoice.name, name)
        oValues.put(Columns.AccountInvoice.origin, origin)
        oValues.put(Columns.AccountInvoice.type, type)
        oValues.put(Columns.AccountInvoice.sent, sent)
        oValues.put(Columns.AccountInvoice.reference, type)
        oValues.put(Columns.AccountInvoice.server_id, serverId)
        oValues.put(Columns.AccountInvoice.number, number)
        oValues.put(Columns.AccountInvoice.company_id, company?.id)
        oValues.put(Columns.AccountInvoice.user_id, user?.id)
        oValues.put(Columns.AccountInvoice.partner_id, customer?.id)
        oValues.put(Columns.AccountInvoice.account_id, account?.id)
        oValues.put(Columns.AccountInvoice.currency_id, currency?.id)
        oValues.put(Columns.AccountInvoice.amount_tax, amountTax)
        oValues.put(Columns.AccountInvoice.amount_paid, amountPaid)
        oValues.put(Columns.AccountInvoice.amount_total, amountTotal)
        oValues.put(Columns.AccountInvoice.amount_return, amountReturn)
        oValues.put(Columns.AccountInvoice.order_date, DateUtils.parseToDB(invoiceDate))
        oValues.put(Columns.AccountInvoice.due_date, DateUtils.parseToDB(dueDate))
        oValues.put(Columns.AccountInvoice.state, state)
        return oValues
    }

    fun addProduct(product: Product): AccountInvoiceLine{
        var total = product.price * 1F
        var posOrderLine = AccountInvoiceLine(0, product.name, company  ,product ,
                "Thanks", product.price, 1F, total, total, 0F, this)
        return posOrderLine
    }

    fun addNewOrderLine(orderLine : AccountInvoiceLine){
        lines.add(orderLine)
    }

    fun removeOrderLine(orderLine : AccountInvoiceLine){
        var orderLinee = lines.find { it?.id == orderLine?.id }
        lines.remove(orderLinee);
    }

    fun encode2SMS() : String{
        var smsMessage = "Rx:"
        lines.forEach {
            smsMessage = smsMessage.plus(it.encode2SMS() + "&")
        }
        return smsMessage.removeSuffix("&")
    }

     fun recalculate(){
         amountTotal = 0F
         amountTax = 0F
        for(orderLine in lines){
            amountTax =  amountTax!! + orderLine.subTotalWithTax
            amountTotal = amountTotal!! + orderLine.subTotalWithoutTax
        }
    }

}

