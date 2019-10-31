package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns.SyncModel
import java.util.Date

class SyncModel(var id: Int, var model: String, var authority: String,
                var syncLimit: Int, var mustSyncRelations: Boolean, var serverCount: Int, var percentageSynced: Int,
                var status: String, var statusDetail: String, var lastSynced: Date) : DTO{

    override fun toOValues(): OValues {
        var oValues = OValues()
        //oValues.put(SyncModel.id, id)
        oValues.put(SyncModel.model, model)
        oValues.put(SyncModel.status, status)
        oValues.put(SyncModel.sync_limit, syncLimit)
        oValues.put(SyncModel.must_sync_relations, mustSyncRelations)
        oValues.put(SyncModel.status_detail, statusDetail)
        oValues.put(SyncModel.server_count, serverCount)
        oValues.put(SyncModel.last_synced, lastSynced)
        return oValues
    }

    companion object{
        val SYNCING = "syncing"
        val SYNCED = "synced"
        val SERVER_COUNT_UPDATE = "api_count_update"
        val API_LIST_UPDATE = "api_list_update"
        val QUEUED = "queued"
        val PROCESSING = "processing"
        val NETWORKISH = "networkish"
        val ERROR = "error"
    }

}