package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

class PosConfig(var id: Int, var serverId: Int, var name: String, var locationId: Int) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.name, name)
        oValues.put(Columns.server_id, serverId)
        return oValues

    }
}