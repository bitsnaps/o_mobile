package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.LazyList

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

data class PosOrderDate(var purchaseDate: Date, var noOfCustomers: Int, var totalAmount: Float, var noOfPurchases: Int) : DTO {

    constructor(purchaseDate: Date): this(purchaseDate, 0, 0F,0)


    private var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private var formatter: DecimalFormat = DecimalFormat("###,###,##0.00")
    lateinit var posOrders: LazyList<PosOrderDate>

    val totalAmountString: String
        get() {
            return "₦ " + formatter.format(totalAmount.toDouble())
        }

    val purchaseDateString: String
        get() {
            return  simpleDateFormat.format(purchaseDate)
        }

    override fun toOValues(): OValues {
        val oValues = OValues()
        return oValues
    }


}
