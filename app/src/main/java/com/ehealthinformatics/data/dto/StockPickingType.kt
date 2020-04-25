package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues

class StockPickingType(var id: Int, var name: String) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}