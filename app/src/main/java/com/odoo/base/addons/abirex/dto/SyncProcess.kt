package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OModel
import com.odoo.core.orm.OValues
import com.odoo.core.rpc.helper.ODomain
import com.odoo.core.utils.ODateUtils
import com.odoo.data.abirex.Columns.SyncProcess
import java.util.Date

class SyncProcess(var id: Int, var oModel: OModel, var syncModel: SyncModel, var parent: String,
                  var children: SyncProcess, var response: String, var serverCount: Int, var savedCount: Int,
                  var status: String, var statusDetail: String, var lastUpdated: Date, var lastSynced: Date) : DTO{

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(SyncProcess.sync_model, syncModel)
        oValues.put(SyncProcess.parent, parent)
        oValues.put(SyncProcess.children, children)
        oValues.put(SyncProcess.response, response)
        oValues.put(SyncProcess.server_count, serverCount)
        oValues.put(SyncProcess.saved_count, savedCount)
        oValues.put(SyncProcess.status, status)
        oValues.put(SyncProcess.status_detail, statusDetail)
        oValues.put(SyncProcess.last_updated, lastUpdated)
        oValues.put(SyncProcess.last_synced, lastSynced)
        return oValues
    }

//    //OModel model, ODomain domain_filter, boolean checkForDataLimit, boolean createRelationRecords
//    private fun getODomain(extraDomain: ODomain): ODomain {
//        val domain = ODomain()
//        domain.append(oModel.defaultDomain())
//        if (extraDomain != null) {
//            domain.append(extraDomain)
//        }
//
//        if (checkForWriteCreateDate) {
//            val serverIds = syncProcess.oModel.serverIds
//            // Model Create date domain filters
//            syncProcess.syncModel.c
//            if (model.checkForCreateDate() && checkForDataLimit) {
//                if (serverIds.size > 0) {
//                    if (model.checkForWriteDate() && !model.isEmptyTable()) {
//                        domain.add("|")
//                    }
//                    if (model.checkForWriteDate() && !model.isEmptyTable()
//                            && createRelationRecords && model.getLastSyncDateTime() != null)
//                        domain.add("&")
//                }
//                val data_limit = preferenceManager.getInt("sync_data_limit", 360)
//                domain.add("create_date", ">=", ODateUtils.getDateBefore(data_limit))
//                if (serverIds.size > 0) {
//                    domain.add("id", "not in", serverIds)
//                }
//            }
//            // Model write date domain filters
//            if (model.checkForWriteDate() && !model.isEmptyTable() && createRelationRecords) {
//                val last_sync_date = model.getLastSyncDateTime()
//                if (last_sync_date != null) {
//                    domain.add("write_date", ">", last_sync_date)
//                }
//            }
//        }
//        return domain
//    }

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