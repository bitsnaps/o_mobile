package com.odoo.base.addons.abirex.dao

import com.odoo.core.orm.ODataRow

interface Dao {

    fun <T> fromRow(oDataRow: ODataRow): T

}
