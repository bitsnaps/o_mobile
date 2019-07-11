package com.odoo.addons.abirex.viewholder

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.View
import android.widget.TextView

import com.odoo.R

class PosOrderDateViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
    public var purchaseDate: TextView
    public var noOfCustomers: TextView
    public var noOfPosOrders: TextView
    public var totalAmount: TextView


    init {
        purchaseDate = itemView.findViewById(R.id.tv_order_date) as TextView
        noOfCustomers = itemView.findViewById(R.id.tv_title) as TextView
        noOfPosOrders = itemView.findViewById(R.id.tv_no_of_orders) as TextView
        totalAmount = itemView.findViewById(R.id.tv_total_amount) as TextView

    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.setHeaderTitle("Select The Action")
        menu.add(0, v.id, 0, "Call")//groupId, itemId, order, title
        menu.add(0, v.id, 0, "SMS")
    }
}