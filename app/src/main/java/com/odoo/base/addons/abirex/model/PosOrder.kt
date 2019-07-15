package com.odoo.base.addons.abirex.model

class PosOrder(var id: Int, var name: String, var session: PosSession, var customer: Customer,
               var amountTax: Float, var amountTotal: Float,var amountPaid: Float, var amountReturn: Float,
               var sequenceNo: Int, var priceList: PriceList){
    lateinit var lines : List<OrderLine>

}

