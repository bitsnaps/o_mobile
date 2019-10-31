package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues

class ProductTemplate(val id: Int, name: String, val active: Boolean, val productType: String) : DTO{
    override fun toOValues(): OValues {
        val oValues = OValues()
        return oValues
    }

}