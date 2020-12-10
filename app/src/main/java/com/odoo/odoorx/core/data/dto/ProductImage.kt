package com.odoo.odoorx.core.data.dto

import android.graphics.Bitmap
import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.utils.BitmapUtils
import com.odoo.odoorx.core.data.db.Columns
import java.io.Serializable

data class ProductImage(var id: Int, var serverId: Int, var image: Bitmap) : DTO, Serializable {

    override fun toString(): String {
        return BitmapUtils.toBase64(image)
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.ProductCol.image, BitmapUtils.toBase64(image))
       
        return oValues
    }

}