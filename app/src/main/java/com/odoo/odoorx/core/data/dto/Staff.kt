package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
//TODO: Sync with hr.employee later
class Staff(partner: Partner, company: Company) :  User(partner.id, partner.serverId, partner, company), DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}