package com.odoo.addons.abirex.viewholder

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

import com.odoo.R

class ProductCategoryViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
    public var image: ImageView
    public var name: TextView
    public var type: TextView
    public var ratingNo: TextView
    public var price: TextView
    public var rating: RatingBar
    public var menu: ImageButton

    init {
        image = itemView.findViewById(R.id.iv_product_image) as ImageView
        name = itemView.findViewById(R.id.tv_product_name) as TextView
        type = itemView.findViewById(R.id.tv_product_type) as TextView
        ratingNo = itemView.findViewById(R.id.tv_product_rating_no) as TextView
        price = itemView.findViewById(R.id.tv_price) as TextView
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