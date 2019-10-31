package com.odoo.base.addons.abirex.dto

import android.graphics.Bitmap
import com.odoo.core.orm.OValues

data class Product(val id: Int, val serverId: Int, val name: String, val active: Boolean,
                   val productTemplate: ProductTemplate, val image: Bitmap?, val imageSmall: Bitmap?,
                   val price: Float, val qtyAvailable: Float,
                   val defaultCode: String?, val code: String?) : DTO {

    override fun toString(): String {
        return name
    }

    override fun toOValues(): OValues {
        val oValues = OValues()
        return oValues
    }

}