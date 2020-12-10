package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.service.SyncStatus
import com.odoo.odoorx.core.base.utils.DateUtils
import com.odoo.odoorx.core.data.db.Columns.SyncModel
import java.util.*

class SyncModel(var id: Int, var modelName: String, var authority: String,
                var syncLimit: Int, var syncDepth: Int, var syncMode: SyncModel.Mode, var mustSyncRelations: Boolean, var serverCount: Int, var localCount: Int,
                var status: String, var statusDetail: String, var lastSynced: Date, var parent: com.odoo.odoorx.core.data.dto.SyncModel?,
                private var children: ArrayList<com.odoo.odoorx.core.data.dto.SyncModel>, var pullToDeviceIds: Set<Integer>, var pushToServerIds: Set<Integer>) : DTO{



    var processUpdated: Date = DateUtils.beginningOfTime()
    var syncProgress = 0
    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(SyncModel.model_name, modelName)
        oValues.put(SyncModel.authority, authority)
        oValues.put(SyncModel.sync_mode, syncMode)
        oValues.put(SyncModel.status, status)
        oValues.put(SyncModel.sync_depth, syncDepth)
        oValues.put(SyncModel.sync_limit, syncLimit)
        oValues.put(SyncModel.must_sync_relations, mustSyncRelations)
        oValues.put(SyncModel.status_detail, statusDetail)
        oValues.put(SyncModel.server_count, serverCount)
        oValues.put(SyncModel.local_count, localCount)
        oValues.put(SyncModel.pull_to_device_ids, if(pullToDeviceIds.isNotEmpty()) pullToDeviceIds.joinToString(",") else "")
        oValues.put(SyncModel.push_to_server_ids, if(pushToServerIds.isNotEmpty()) pushToServerIds.joinToString(",") else "")
        oValues.put(SyncModel.last_synced, DateUtils.parseToDB(lastSynced))
        oValues.put(SyncModel.process_updated, DateUtils.parseToDB(processUpdated))
        oValues.put(SyncModel.parent_id, if(parent?.id != null) parent?.id else "false")
        return oValues
    }


    override fun toString(): String {
        return  if(parent == null) modelName else "${parent.toString()} >> $modelName"
    }

    fun addChild(syncModel: com.odoo.odoorx.core.data.dto.SyncModel){
        children.add(syncModel)
    }

    fun getChildren(): Iterator<com.odoo.odoorx.core.data.dto.SyncModel>{
        return children.iterator()
    }

    fun isCompleted(): Boolean{
        return status == SyncStatus.COMPLETED
    }

    fun isQueued(): Boolean{
        return status == SyncStatus.QUEUED
    }

    fun hasChildren(): Boolean {
        return children.isNotEmpty()
    }

    fun requeue(){
            status = SyncStatus.QUEUED
            localCount = 0
            serverCount = 0
            val children = getChildren()
            while (children.hasNext()) {
                val childSyncModel = children.next()
                childSyncModel.requeue()

            }
    }

    fun getChildCount(): SyncProgressCount {
        var total = serverCount
        var count = localCount
        val children = getChildren()
        while (children.hasNext()) {
            val childSyncModel = children.next()
            if (childSyncModel.hasChildren()) {
                val syncProgressCount = childSyncModel.getChildCount()
                return SyncProgressCount(syncProgressCount.count + count,
                        syncProgressCount.total + serverCount)
            } else {
                total += serverCount
                count += localCount
            }
        }
        return SyncProgressCount(count, total)
    }

    fun percentage(): Int {
        if(serverCount == 0 ){
            return if (isCompleted()) 100 else  0
        }
        return ((localCount.toFloat() / serverCount.toFloat()) * 100).toInt()
    }

    fun realPercentage(): Int {
        return if(hasChildren()){
            (percentage()/2) + (childrenPercentage()/2)
        } else {
            percentage()
        }
    }

    fun childrenPercentage(): Int{
        var childrenPercent = 0
        for(childSyncModel in children){
            childrenPercent += (childSyncModel.realPercentage() / children.size)
        }
        return childrenPercent
    }

    class SyncProgressCount  constructor(public var count: Int, public var total: Int)

}