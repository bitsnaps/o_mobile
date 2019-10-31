package com.odoo.addons.abirex.viewholder

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.odoo.App

import com.odoo.R
import com.odoo.addons.abirex.detail.PosOrderDetails
import com.odoo.core.utils.IntentUtils

class SyncModelViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    public var name: TextView = itemView.findViewById(R.id.tvSyncModelName) as TextView
    public var count: TextView = itemView.findViewById(R.id.tvSyncModelCount) as TextView
    public var status: TextView = itemView.findViewById(R.id.tvSyncModelStatus) as TextView
    public var statusDetail: TextView = itemView.findViewById(R.id.tvSyncModelStatusDetail) as TextView
    public var btnTriggerSync: Button = itemView.findViewById(R.id.btnSyncTrigger) as Button
    public var lastSynced: TextView = itemView.findViewById(R.id.tvSyncModelLastSyncTime) as TextView

    override fun onClick(v: View?) {

    }

}