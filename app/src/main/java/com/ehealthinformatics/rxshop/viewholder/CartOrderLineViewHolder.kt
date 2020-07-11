package com.ehealthinformatics.rxshop.viewholder

import android.view.View
import android.widget.*

import com.ehealthinformatics.odoorx.rxshop.R

class CartOrderLineViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

//    public var id = itemView.findViewById(R.id.tv_product_id) as TextView
    public var name = itemView.findViewById(R.id.tv_cart_product_name) as TextView
    public var desc = itemView.findViewById(R.id.tv_cart_product_desc) as TextView
    public var price = itemView.findViewById(R.id.tv_cart_product_price) as TextView
    public var qty = itemView.findViewById(R.id.tv_cart_product_qty) as TextView
    public var add  = itemView.findViewById(R.id.ib_cart_product_add) as ImageButton
    public var remove = itemView.findViewById(R.id.ib_cart_product_remove) as ImageButton

}