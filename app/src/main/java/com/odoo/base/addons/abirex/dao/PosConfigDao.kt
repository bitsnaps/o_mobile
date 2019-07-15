package com.odoo.base.addons.abirex.dao

import android.content.Context

import com.odoo.base.addons.abirex.model.PosConfig
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.orm.fields.types.OVarchar
import com.odoo.core.support.OUser

class PosConfigDao(context: Context, user: OUser?) : OModel(context, "pos.config", user) {
    internal var name = OColumn("Name", OVarchar::class.java)

    operator fun get(id: Int): PosConfig {
        val oDataRow = browse(id)
        return fromDataRow(oDataRow)
    }

    fun fromDataRow(row: ODataRow): PosConfig {
        val id = row.getInt(OColumn.ROW_ID)
        val name = row.getString(this.name.name)
        return PosConfig(id!!, name)
    }

    override fun allowCreateRecordOnServer(): Boolean {
        return false
    }

    override fun allowUpdateRecordOnServer(): Boolean {
        return false
    }

    override fun allowDeleteRecordInLocal(): Boolean {
        return false
    }

    companion object {
        val TAG = PosConfigDao::class.java.simpleName
    }

}
