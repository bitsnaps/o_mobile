package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.data.db.Columns

data class PriceList(var id: Int, var serverId: Int, var name: String, var currency: Currency) : DTO{

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.server_id, serverId)
        oValues.put(Columns.name, name)
        return oValues
    }

}