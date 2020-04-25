package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.db.Columns

class Category(var id: Int, var name: String) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        return oValues
    }
}