package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

class Customer(partner: Partner) : User(partner.id, partner.serverId, partner), DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        return oValues
    }

}