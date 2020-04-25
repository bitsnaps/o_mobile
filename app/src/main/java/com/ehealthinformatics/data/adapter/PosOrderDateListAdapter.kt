package com.ehealthinformatics.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup

import com.ehealthinformatics.R
import com.ehealthinformatics.app.viewholder.PosOrderDateViewHolder
import com.ehealthinformatics.data.dto.PosOrderDate
import com.ehealthinformatics.data.LazyList

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
