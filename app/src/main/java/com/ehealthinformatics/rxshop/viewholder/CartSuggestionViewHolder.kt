package com.ehealthinformatics.rxshop.viewholder

import android.view.View
import android.widget.*

import androidx.recyclerview.widget.RecyclerView

import com.ehealthinformatics.odoorx.rxshop.R

class CartSuggestionViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    var name: TextView = itemView.findViewById(R.id.tv_search_product_name) as TextView
    var uom: TextView = itemView.findViewById(R.id.tv_search_product_uom) as TextView
    var price: TextView = itemView.findViewById(R.id.tv_search_product_price) as TextView
    var add: Button = itemView.findViewById(R.id.btn_search_product_add) as Button

}