package com.ehealthinformatics.data.dto

import android.content.SyncResult
import android.util.Log
import com.ehealthinformatics.data.dao.IrModel
import com.ehealthinformatics.core.orm.OModel
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.core.rpc.helper.ODomain
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooResult
import com.ehealthinformatics.core.service.OSyncDataUtils
import com.ehealthinformatics.core.service.SyncStatus.*
import com.ehealthinformatics.core.support.OUser
import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.core.utils.ODateUtils
import com.ehealthinformatics.data.db.Columns
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.absoluteValue

class SyncProcess(var parent: SyncProcess?, var oModel: OModel, var syncModelDao: IrModel, var oUser: OUser, var syncModel: SyncModel,
                  var dataUtils: OSyncDataUtils?, var filterDomain: ODomain, var syncResult: SyncResult) {


    var response = ""
    var serverCount = 0
    var savedCount = 0
    var status = 0;
    lateinit var syncResponse: OdooResult
    val messageQueue: Queue<String> = LinkedList<String>()
    var percentage = 0
    private var children = HashMap<String, SyncProcess>()

    fun getModelName(): String{
        return syncModel.modelName
    }

    fun savedRecord (processed: Int, totalSize: Int) {
        val oValues = OValues()

        syncModel.localCount = processed
        var newPercentage = syncModel.realPercentage()
        if(syncModel.serverCount < 100){
            oValues.put(Columns.SyncModel.local_count,  syncModel.localCount)
            syncModelDao.update(syncModel.id, oValues)
        } else {
            if(newPercentage != percentage){
                oValues.put(Columns.SyncModel.local_count,  syncModel.localCount)
                syncModelDao.update(syncModel.id, syncModel.toOValues())
                syncModelDao.update(syncModel.id, oValues)
            }
        }
        percentage = newPercentage
    }

    fun update(status: String, _detail: String){
        var detail = "[$syncModel] - $_detail"
        messageQueue.add(detail)
        val stepDone = java.util.HashMap<String, Boolean>()
        when (status) {
            QUEUED -> syncModel.statusDetail = "Sync Process Queued"
            STARTED -> {
                oModel.onSyncStarted()
                syncModel.statusDetail = "Started Sync Process"
            }
            API_CALLED -> syncModel.statusDetail = "Sending Request"
            SERVER_RESPONSE_RECEIVED -> syncModel.statusDetail = "Response received"
            NULL_RESONSE -> syncModel.statusDetail = "Nothing received"
            ERROR_RESPONSE -> syncModel.statusDetail = "Server error occurred"
            PRE_PROCESSING -> syncModel.statusDetail = "Processing records"
            PROCESSING -> syncModel.statusDetail = "Processing and saving records"
            SAVED_RECORDS -> syncModel.statusDetail = "Completed saving records"
            SERVER_UPDATE -> syncModel.statusDetail = "Updating locally changed records"
            CHILD_SYNC_CREATED -> syncModel.statusDetail = "Creating Child process"
            CHILD_SYNC_FINISHED -> syncModel.statusDetail = "Finished Child processes"
            COMPLETED -> {
                oModel.onSyncFinished()
                syncModel.statusDetail = "Completed Sync Process"
                syncModel.localCount = 0
            }
        }

        syncModel.status = status
        syncModel.processUpdated = DateUtils.now()

        if (status == PRE_PROCESSING){
            syncModel.serverCount = serverCount
        }else if(syncModel.isCompleted()){
            syncModel.pullToDeviceIds = HashSet()
            syncModel.pushToServerIds =  HashSet()
            syncModel.lastSynced = DateUtils.now()
        }
        if(stepDone.get(status) == null){
            stepDone[status] = true
            syncModelDao.update(syncModel.id, syncModel.toOValues())
        }


        Log.d(":SyncProcess", pol())
    }

    private fun pol() : String {
        return messageQueue.poll()
    }

    fun addChild(syncProcess: SyncProcess){
        syncProcess.parent = this
        children[syncProcess.getModelName()] = syncProcess
    }

    fun getChildModel(modelName: String): SyncModel? {
        return children[modelName]?.syncModel
    }

    fun getChildren(): Iterator<SyncProcess>{
        return children.values.iterator()
    }

    fun getChildrenNames(): Set<String>{
        return  children.keys
    }

    fun getODomain(): ODomain {
        //TODO: Handle if it's worth calling again when we have old value FORCE_CALL
        val domain = ODomain()
        if(parent == null)
        domain.append(oModel.defaultDomain())
        domain.append(filterDomain)
        val syncMode = syncModel.syncMode


        var checkForWriteCreateDate =
                //Parent Triggered is nnot syncing children
                (syncMode == Columns.SyncModel.Mode.REFRESH_TRIGGERED || syncMode == Columns.SyncModel.Mode.SYSTEM_TRIGGERED)
        val checkForDataLimit = checkForWriteCreateDate
        val createRelationRecords = syncModel.syncLimit > 0

        if (checkForWriteCreateDate) {
            val serverIds = oModel.serverIds
            // Model Create date domain filters
            if (oModel.checkForCreateDate() && checkForDataLimit) {
                if (serverIds.size > 0) {
                    if (oModel.checkForWriteDate() && !oModel.isEmptyTable) {
                        domain.add("|")
                    }
                    if (oModel.checkForWriteDate() && !oModel.isEmptyTable
                            && createRelationRecords)
                        domain.add("&")
                }

                domain.add("create_date", ">=", ODateUtils.getDateBefore(syncModel.syncLimit))
                if (serverIds.size > 0) {
                    domain.add("id", "not in", serverIds)
                }
            }
            // Model write date domain filters
            if (oModel.checkForWriteDate() && !oModel.isEmptyTable() && createRelationRecords) {
                val last_sync_date = ODateUtils.getDate(syncModel.lastSynced, ODateUtils.DEFAULT_FORMAT)
                if (last_sync_date != null) {
                    domain.add("write_date", ">", last_sync_date)
                }
            }
        }

        if (syncModel.pullToDeviceIds.isNotEmpty()) {
            val idFilterDommain = ODomain()
            idFilterDommain.add("id", "in",  ArrayList(syncModel.pullToDeviceIds))
            domain.append(idFilterDommain)
        }

        return domain
    }

    fun sameGrandParent(): Boolean {
       return parent?.parent?.syncModel?.modelName.equals(syncModel.modelName);
    }

    override fun toString() : String{
        return syncModel.modelName
    }


}