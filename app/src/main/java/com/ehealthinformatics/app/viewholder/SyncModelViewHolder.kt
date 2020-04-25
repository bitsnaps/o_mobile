package com.ehealthinformatics.app.viewholder

import android.view.View
import android.widget.*

import com.ehealthinformatics.R

class SyncModelViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    public var name: TextView = itemView.findViewById(R.id.tv_sync_model_name) as TextView
    public var children: TextView = itemView.findViewById(R.id.tv_children) as TextView
    public var count: TextView = itemView.findViewById(R.id.tvSyncModelCount) as TextView
    public var status: TextView = itemView.findViewById(R.id.tvSyncModelStatus) as TextView
    public var statusDetail: TextView = itemView.findViewById(R.id.tvSyncModelStatusDetail) as TextView
    public var btnTriggerSync: Button = itemView.findViewById(R.id.btnSyncTrigger) as Button
    public var lastSynced: TextView = itemView.findViewById(R.id.tvSyncModelLastSyncTime) as TextView
    public var syncProgress = itemView.findViewById(R.id.pbSyncProgress) as ProgressBar
    public var btnToggleExpand: ImageButton = itemView.findViewById(R.id.btn_toggle_expand)
    public var lyt_expand: LinearLayout = itemView.findViewById(R.id.lyt_expand)
    public var lyt_parent: LinearLayout = itemView.findViewById(R.id.lyt_parent)
    var expanded = false;

    override fun onClick(v: View?) {

    }

}