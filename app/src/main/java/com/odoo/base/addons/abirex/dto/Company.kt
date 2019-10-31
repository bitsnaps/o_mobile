package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

class Company(var id: Int, var serverId: Int, var name: String, private var partner: Partner): DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        return oValues
    }

}