package com.odoo.rxshop.viewholder

import android.view.ContextMenu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

import com.odoo.odoorx.rxshop.R

class ProductCategoryViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
    public var image: ImageView
    public var name: TextView
    public var customer: TextView
    public var state: TextView
    public var totalCost: TextView
    public var rating: RatingBar
    public var menu: ImageButton

    init {
        image = itemView.findViewById(R.id.iv_product_image) as ImageView
        name = itemView.findViewById(R.id.tv_pos_order_name) as TextView
        customer = itemView.findViewById(R.id.tv_pos_order_customer) as TextView
        state = itemView.findViewById(R.id.tv_pos_order_state) as TextView
        totalCost = itemView.findViewById(R.id.tv_pos_order_total) as TextView
        rating = itemView.findViewById(R.id.rb_product_rating) as RatingBar
        menu = itemView.findViewById(R.id.ib_product_menu) as ImageButton
        menu.setOnCreateContextMenuListener(this)

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.setHeaderTitle("Select The Action")
        menu.add(0, v.id, 0, "Call")//groupId, itemId, order, title
        menu.add(0, v.id, 0, "SMS")
    }
}