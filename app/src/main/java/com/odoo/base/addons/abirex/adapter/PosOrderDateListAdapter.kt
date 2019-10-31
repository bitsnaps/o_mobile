package com.odoo.base.addons.abirex.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.odoo.R
import com.odoo.addons.abirex.viewholder.PosOrderDateViewHolder
import com.odoo.base.addons.abirex.dto.PosOrderDate
import com.odoo.data.LazyList

class PosOrderDateListAdapter public constructor(private var posOrderDateLazyList: LazyList<PosOrderDate>
                                                     ) : RecyclerView.Adapter<PosOrderDateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosOrderDateViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_customer_product, null)
        return PosOrderDateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosOrderDateViewHolder, position: Int) {
        val posOrderDate = posOrderDateLazyList[position]
        holder.noOfCustomers.text = "${posOrderDate.noOfCustomers}"
        holder.noOfPosOrders.text = "${posOrderDate.noOfPurchases}"
        holder.purchaseDate.text = posOrderDate.purchaseDateString
        holder.totalAmount.text = posOrderDate.totalAmountString


    }

    override fun getItemCount(): Int {
        return  posOrderDateLazyList.size
    }

}
