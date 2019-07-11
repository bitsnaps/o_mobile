package com.odoo.base.addons.abirex.model

import android.graphics.Bitmap

data class Product(var id: Int, var name: String, var active: Boolean,
                   var productTemplate: ProductTemplate, var image: Bitmap?, var imageSmall: Bitmap?,
                   var price: Float, var qtyAvailable: Float,
                   var defaultCode: String?, var code: String?) {

}