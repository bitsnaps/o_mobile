package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
//TODO: Sync with hr.employee later
class Staff(partner: Partner) :  User(partner.id, partner.serverId, partner), DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}