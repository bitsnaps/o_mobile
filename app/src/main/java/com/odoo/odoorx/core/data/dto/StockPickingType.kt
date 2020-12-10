package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues

class StockPickingType(var id: Int, var name: String) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}