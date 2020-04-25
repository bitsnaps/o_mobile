package com.ehealthinformatics.data.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.ehealthinformatics.R
import com.ehealthinformatics.app.viewholder.PurchaseOrderViewHolder
import com.ehealthinformatics.data.dto.PurchaseOrder

class PurchaseOrderListAdapter() :
        RecyclerView.Adapter< PurchaseOrderViewHolder>() {

    private lateinit var purchaseOrderList: List<PurchaseOrder>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  PurchaseOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_purchase_order, null)
        return  PurchaseOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PurchaseOrderViewHolder, position: Int) {
        val purchaseOrder = purchaseOrderList[position]
        holder.name.text = purchaseOrder.name
        holder.state.text = purchaseOrder.state
        holder.state.setTextColor(if(purchaseOrder.serverId > 0) Color.GREEN else Color.RED)
        holder.vendor.text = purchaseOrder.vendor.displayName
        holder.totalCost.text = "₦ ${purchaseOrder.amountTotal}"
        
    }

    override fun getItemCount(): Int {
        return if(::purchaseOrderList.isInitialized)  purchaseOrderList.size else return 0
    }

    fun getItem(position: Int):  PurchaseOrder {
        return purchaseOrderList[position]
    }

    fun reset(purchaseOrderList: List<PurchaseOrder>) {
        this.purchaseOrderList = purchaseOrderList
        notifyDataSetChanged()
    }

}
