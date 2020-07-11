package com.ehealthinformatics.rxshop.viewholder

import android.view.View
import android.widget.*

import androidx.recyclerview.widget.RecyclerView

import com.ehealthinformatics.odoorx.rxshop.R

class SuggestionViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var parentLayout: LinearLayout = itemView.findViewById(R.id.ll_suggestion_parent_layout) as LinearLayout
    var name: TextView = itemView.findViewById(R.id.tv_suggestion_name) as TextView
    var detail: TextView = itemView.findViewById(R.id.tv_suggestion_detail) as TextView
}