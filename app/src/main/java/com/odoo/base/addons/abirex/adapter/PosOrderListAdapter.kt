package com.odoo.base.addons.abirex.adapter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.odoo.App

import com.odoo.R
import com.odoo.addons.abirex.detail.PosOrderDetails
import com.odoo.addons.abirex.viewholder.PosOrderViewHolder
import com.odoo.base.addons.abirex.dto.PosOrder
import com.odoo.core.utils.IntentUtils
import com.odoo.data.LazyList

class PosOrderListAdapter constructor(private val contextMenuCallback: ContextMenuCallback
                                                     ) : RecyclerView.Adapter<PosOrderViewHolder>() {

    private lateinit var posOrderLazyList: LazyList<PosOrder>
    private var listStyle = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(if (listStyle) R.layout.layout_list_item_customer_product else R.layout.layout_grid_item_customer_product, null)

        return PosOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosOrderViewHolder, position: Int) {
        val posOrder = posOrderLazyList[position]
        holder.id.text = posOrder.id.toString()
        holder.name.text = posOrder.name
        holder.customerName.text = posOrder.customer.displayName
        holder.sessionName.text = posOrder.session.name
        holder.amountTaxInc.text = "₦ ${posOrder.amountTax}"
        holder.amountPaid.text = "₦ ${posOrder.amountPaid}"
        holder.amountReturn.text = "₦ ${posOrder.amountReturn}"
        holder.totalAmount.text = "₦ ${posOrder.amountTotal}"
        holder.itemView.setOnClickListener {
            val posOrderId = posOrderLazyList[position].id
            val data = Bundle()
            if (posOrderId != null) {
                data.putInt("pos_order_id", posOrderId)
            }
            IntentUtils.startActivity(App.getContext(), PosOrderDetails::class.java, data)
        }
    }

    override fun getItemCount(): Int {
        return if(::posOrderLazyList.isInitialized)  posOrderLazyList.size else return 0
    }

    fun getItem(position: Int): PosOrder {
        return posOrderLazyList[position]
    }

    fun changeList(posOrderLazyList: LazyList<PosOrder>) {
        this.posOrderLazyList = posOrderLazyList
        notifyDataSetChanged()
    }

    fun resetList() {
        (this.posOrderLazyList.list as ArrayList).clear()
        notifyDataSetChanged()
    }

    interface ContextMenuCallback {
        fun onContextMenuClick(view: ImageButton, id: Int, title: String)
    }

    private fun setOnClickListener(view: ImageButton, id: Int, title: String) {
        view.setOnClickListener {
            contextMenuCallback.onContextMenuClick(view, id, title)
        }
    }

}
