package com.odoo.base.addons.abirex.model

class OrderLine(var id: Int,var company: Company,  var product: Product, var name: String,
                var notice: String, var unitPrice: Float, var quantity: Float,
                var subTotalWithoutTax: Float, var subTotalWithTax: Float, var discount: Float,
                var order: String
                ) {
}