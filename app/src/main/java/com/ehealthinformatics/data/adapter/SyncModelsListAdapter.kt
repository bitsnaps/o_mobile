package com.ehealthinformatics.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ehealthinformatics.BuildConfig

import com.ehealthinformatics.R
import com.ehealthinformatics.app.utils.Tools
import com.ehealthinformatics.app.utils.ViewAnimation
import com.ehealthinformatics.app.viewholder.SyncModelViewHolder
import com.ehealthinformatics.data.dto.SyncModel
import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.core.support.sync.SyncUtils
import com.ehealthinformatics.core.utils.StringUtils
import kotlin.math.absoluteValue

class SyncModelsListAdapter constructor(val syncUtils : SyncUtils) : androidx.recyclerview.widget.RecyclerView.Adapter<SyncModelViewHolder>() {

    private lateinit var irModelLazyList: List<SyncModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncModelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_sync_model, parent, false)
        return SyncModelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SyncModelViewHolder, position: Int) {
        val syncModel = irModelLazyList[position]
        holder.name.text = StringUtils.toTitleCase(syncModel.modelName.replace(".", " "))
        holder.count.text = syncModel.localCount.toString() + " of " + syncModel.serverCount.toString()
        holder.status.text = syncModel.status
        holder.statusDetail.text = syncModel.statusDetail
        var children = ""

        for (syncModel in syncModel.getChildren()){
            children = children + "\n" + syncModel.modelName
        }
        holder.children.text = children
        holder.lastSynced.text = DateUtils.formatToLongDate(syncModel.lastSynced)
        if(syncModel.serverCount> 0){
            holder.syncProgress.progress = (syncModel.serverCount - syncModel.localCount).absoluteValue * 100 / syncModel.serverCount
        }
        holder.btnTriggerSync.setOnClickListener {
            val authority = BuildConfig.APPLICATION_ID + ".core.provider.content.sync." + syncModel.modelName.replace(".", "_")
            syncUtils.requestSync(authority)
        }
        holder.btnToggleExpand.setOnClickListener{
            val show = toggleLayoutExpand(holder.expanded, it, holder.lyt_expand)
            holder.expanded = show
        }

    }

    override fun getItemCount(): Int {
        return if(::irModelLazyList.isInitialized)  irModelLazyList.size else return 0
    }

    fun getItem(position: Int): SyncModel {
        return irModelLazyList[position]
    }

    fun changeList(irModelLazyList: List<SyncModel>) {
        this.irModelLazyList = irModelLazyList
        notifyDataSetChanged()
    }

    private fun toggleLayoutExpand(show: Boolean, view: View, lyt_expand: View): Boolean {
        Tools.toggleArrow(show, view)
        if (show) {
            ViewAnimation.expand(lyt_expand)
        } else {
            ViewAnimation.collapse(lyt_expand)
        }
        return show
    }

}
