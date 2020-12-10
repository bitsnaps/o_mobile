package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.data.db.Columns

class Category(var id: Int, var name: String) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        return oValues
    }
}