package com.ehealthinformatics.data.dto

import android.content.Context
import android.graphics.Bitmap
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.core.utils.BitmapUtils
import java.io.Serializable

data class Product(var id: Int, var serverId: Int, var name: String, var description: String, var active: Boolean,
                   var productTemplate: ProductTemplate, var imageMedium: Bitmap?, var imageSmall: Bitmap?, var imageLarge: Bitmap?,
                   var price: Float, var qtyAvailable: Float,
                   var defaultCode: String?, var code: String?, var cost: Float?) : DTO, Serializable {

    override fun toString(): String {
        return name
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }

    fun imageValues(context: Context, bitmap: Bitmap): OValues {
        val values =  OValues()
        val bitmapSmall = BitmapUtils.resizeImage(72, 72, bitmap)
        val bitmapMedium = BitmapUtils.resizeImage(144, 144, bitmap)
        val bitmapLarge = BitmapUtils.resizeImage(480, 800, bitmap)
        values.put("image_small", BitmapUtils.toBase64(bitmapSmall))
        values.put("image_medium", BitmapUtils.toBase64(bitmapMedium))
        values.put("imageMedium", BitmapUtils.toBase64(bitmapLarge));
        return values;
    }

}