package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

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