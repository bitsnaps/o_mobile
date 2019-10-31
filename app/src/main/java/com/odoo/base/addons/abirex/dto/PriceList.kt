package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

class PriceList(var id: Int, var serverId: Int, var name: String) : DTO{

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.server_id, serverId)
        oValues.put(Columns.name, name)
        return oValues
    }

}