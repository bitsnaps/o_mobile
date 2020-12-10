package com.odoo.odoorx.core.data.dao

import com.odoo.odoorx.core.base.orm.ODataRow
import com.odoo.odoorx.core.data.dto.DTO

interface Dao {

    fun <T> fromRow(oDataRow: ODataRow): T
    fun <T> get(id: Int) : T
    fun <T> put(dto: DTO) : Boolean

}
