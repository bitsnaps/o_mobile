package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues

open class User(var id: Int, var serverId: Int?, var partner: Partner?, var company: Company) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }

    public override fun toString(): String {
        return "id: {$id}"
    }


}