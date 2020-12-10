package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.utils.DateUtils
import com.odoo.odoorx.core.data.db.Columns
import java.util.*
import kotlin.collections.ArrayList

class PosOrder(var id: Int?,  var serverId: Int?, var name: String?, var company: Company?, var session: PosSession?, var user: User?,
               var customer: Customer?, var priceList: PriceList?,  var currencyRate: Float?,
               var amountTax: Float?, var amountTotal: Float?,var amountPaid: Float?, var amountReturn: Float?,
               var sequenceNo: Int?, var state: String?, var orderDate: Date?,
               var lines : ArrayList<PosOrderLine> = ArrayList()) : DTO {

    constructor(name: String, company: Company, session: PosSession, user: User, customer: Customer, priceList: PriceList?, currencyRate: Float, sequenceNo: Int):
            this(0, 0, name, company, session, user, customer, priceList, currencyRate, 0F, 0F, 0F,0F,
                    sequenceNo,"draft", DateUtils.now())

   val accountBankStatements = ArrayList<AccountBankStatement>()


    fun addStatement(statement : AccountBankStatement){
        accountBankStatements.add(statement)
    }

    public fun getStatementLines() : List<AccountBankStatementLine> {
        val lines = ArrayList<AccountBankStatementLine>()
        for (statement in accountBankStatements){
            lines.addAll(statement.statements)
        }
        return lines
    }

//TODO: Change State to enum
    override fun toOValues(): OValues {
        val oValues = OValues()
        oValues.put(Columns.PosOrder.name, name)
        oValues.put(Columns.PosOrder.server_id, serverId)
        oValues.put(Columns.PosOrder.sequence_no, sequenceNo)
        oValues.put(Columns.PosOrder.session_id, session?.id)
        oValues.put(Columns.PosOrder.company_id, company?.id)
        oValues.put(Columns.PosOrder.user_id, user?.id)
        oValues.put(Columns.PosOrder.partner_id, customer?.id)
        oValues.put(Columns.PosOrder.currency_rate, currencyRate)
        oValues.put(Columns.PosOrder.amount_tax, amountTax)
        oValues.put(Columns.PosOrder.amount_paid, amountPaid)
        oValues.put(Columns.PosOrder.amount_total, amountTotal)
        oValues.put(Columns.PosOrder.amount_return, amountReturn)
        oValues.put(Columns.PosOrder.pricelist_id, priceList?.id)
        oValues.put(Columns.PosOrder.order_date, orderDate)
        oValues.put(Columns.PosOrder.state, state)
        return oValues
    }

    fun addProduct(product: Product): PosOrderLine{
        var total = product.price * 1F
        var posOrderLine = PosOrderLine(0, product.name,  company  , product ,
                "Thanks", product.price, 1F, total, total, 0F, this)
        return posOrderLine
    }

    fun addNewOrderLine(orderLine : PosOrderLine){
        lines.add(orderLine)
    }

    fun removeOrderLine(orderLine : PosOrderLine){
        var orderLinee = lines.find { it.id == orderLine.id }
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
            amountTax = amountTax  as  Float + orderLine.subTotalWithTax!!
            amountTotal = amountTotal as Float + orderLine.subTotalWithoutTax!!
        }
    }

    fun amountPaidWithChange() : Float {
        return if (getStatementLines().isNotEmpty()) {
            var paymentLinesTotal = calculatePaymentLinesTotal()
            paymentLinesTotal + amountReturn!!
        } else {
            amountPaid!! + amountReturn!!
        }
    }


    fun calculateBalance () : Float {
        var totalPaid = 0.0F
        if(getStatementLines().isNotEmpty()){
            for(paymentLine in getStatementLines()) {
                totalPaid += (if(paymentLine.id!! > 0) {paymentLine.amount!! } else 0.0F)
            }
        } else {
            totalPaid = amountPaid!!
        }

        return amountTotal!! - totalPaid
    }

    fun calculatePaymentLinesTotal () : Float {
        var total = 0.0F
        for(paymentLine in getStatementLines()) {
            total += paymentLine.amount!!
        }
        return total
    }

    fun calculateCreatedPaymentLinesTotal () : Float {
        var total = 0.0F
        for(paymentLine in getStatementLines()) {
            total += if(paymentLine.id!! > 0) { paymentLine.amount!! } else { 0F }
        }
        return total
    }



}

