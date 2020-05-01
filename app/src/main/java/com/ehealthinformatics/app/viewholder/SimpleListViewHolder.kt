package com.ehealthinformatics.app.viewholder

import android.view.View
import android.widget.*

import androidx.recyclerview.widget.RecyclerView

import com.ehealthinformatics.R

class SimpleListViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    val linearLayout: LinearLayout  = itemView.findViewById(R.id.ll_simple_item)
    val label: TextView = itemView.findViewById(R.id.tv_label) as TextView
    //var hiddenValue: TextView = itemView.findViewById(R.id.tv_value) as TextView
}