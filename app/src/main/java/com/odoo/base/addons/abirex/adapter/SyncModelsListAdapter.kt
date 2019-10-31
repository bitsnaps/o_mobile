package com.odoo.base.addons.abirex.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.odoo.R
import com.odoo.addons.abirex.viewholder.PosOrderViewHolder
import com.odoo.addons.abirex.viewholder.SyncModelViewHolder
import com.odoo.base.addons.abirex.dao.ProductDao
import com.odoo.base.addons.abirex.dto.SyncModel
import com.odoo.base.addons.abirex.util.DateUtils
import com.odoo.core.orm.OModel
import com.odoo.core.support.sync.SyncUtils

class SyncModelsListAdapter constructor(val syncUtils : SyncUtils) : RecyclerView.Adapter<SyncModelViewHolder>() {

    private lateinit var irModelLazyList: List<SyncModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncModelViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_sync_model, null)

        return SyncModelViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SyncModelViewHolder, position: Int) {
        val syncModel = irModelLazyList[position]
        holder.name.text = syncModel.model
        holder.count.text = syncModel.percentageSynced.toString() + " of " + syncModel.serverCount.toString()
        holder.status.text = syncModel.status
        holder.statusDetail.text = syncModel.statusDetail
        holder.lastSynced.text = DateUtils.formatToLongDate(syncModel.lastSynced)
        holder.btnTriggerSync.setOnClickListener {
            syncUtils.requestSync(syncModel.authority)
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

    fun resetList() {
        (this.irModelLazyList as ArrayList).clear()
        notifyDataSetChanged()
    }

}
