package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues

class ProductTemplate(var id: Int?, var  name: String?, var active: Boolean?, var isMedicine: Boolean?, var description: String?, var uom: Uom?, var category: Category?) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }

}