package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues

open class User(var id: Int, var serverId: Int, var partner: Partner) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }

}