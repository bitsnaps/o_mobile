package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.db.Columns
import java.io.Serializable

data class ProductImage(var id: Int, var serverId: Int, var image: String) : DTO, Serializable {

    override fun toString(): String {
        return image
    }

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.ProductCol.image, image)
       
        return oValues
    }

}