package com.ehealthinformatics.odoorx.rxshop.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.odoorx.core.data.LazyList
import com.ehealthinformatics.rxshop.viewholder.PosOrderDateViewHolder
import com.ehealthinformatics.odoorx.core.data.dto.PosOrderDate

class PosOrderDateListAdapter public constructor(private var posOrderDateLazyList: LazyList<PosOrderDate>
                                                     ) : androidx.recyclerview.widget.RecyclerView.Adapter<PosOrderDateViewHolder>() {

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
