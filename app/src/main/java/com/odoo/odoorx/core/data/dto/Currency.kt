package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.data.db.Columns

class Currency(var id: Int, var serverId: Int, var name: String, var symbol: String, var rate: Float) : DTO {
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.server_id, serverId)
        oValues.put(Columns.name, name)
        oValues.put(Columns.Currency.symbol, symbol)
        oValues.put(Columns.Currency.rate, rate)
        return oValues
    }
}