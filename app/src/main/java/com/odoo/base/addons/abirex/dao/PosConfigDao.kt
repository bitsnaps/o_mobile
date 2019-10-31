package com.odoo.base.addons.abirex.dao

import android.content.Context
import com.odoo.BuildConfig

import com.odoo.base.addons.abirex.dto.PosConfig
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.OO2MRecord
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.orm.fields.types.OVarchar
import com.odoo.core.support.OUser
import com.odoo.data.abirex.Columns
import com.odoo.data.abirex.ModelNames

class PosConfigDao(context: Context, user: OUser?) : OModel(context, ModelNames.POS_CONFIG, user) {
    internal var name = OColumn("Name", OVarchar::class.java)
    internal var location_id = OColumn("Location", LocationDao::class.java, OColumn.RelationType.ManyToOne)

    override operator fun get(id: Int): PosConfig {
        val oDataRow = browse(id)
        return fromDataRow(oDataRow)
    }

    fun fromDataRow(row: ODataRow): PosConfig {
        val id = row.getInt(Columns.id)
        val serverId = row.getInt(Columns.server_id)
        val name = row.getString(this.name.name)
        val locationId = row.getInt(this.location_id.name)
        return PosConfig(id, serverId, name, locationId)
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
        @JvmField
        var AUTHORITY: String = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_config"
        val TAG = PosConfigDao::class.java.simpleName
    }

}
