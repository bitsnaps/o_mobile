package com.ehealthinformatics.data.adapter

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu

import com.ehealthinformatics.R
import com.ehealthinformatics.app.listeners.OnItemClickListener
import com.ehealthinformatics.app.listeners.OnMoreButtonClickListener
import com.ehealthinformatics.app.viewholder.PosOrderViewHolder
import com.ehealthinformatics.config.OConstants
import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.data.dto.PosOrder

class PosOrderListAdapter(private var context: Context, private val onItemClickListener: OnItemClickListener<PosOrder>, private var onMoreButtonClickListener: OnMoreButtonClickListener) :
        RecyclerView.Adapter<PosOrderViewHolder>() {

    private lateinit var posOrderList: List<PosOrder>
    private var listStyle = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PosOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(if (listStyle) R.layout.layout_list_item_customer_product else R.layout.layout_grid_item_customer_product, null)
        return PosOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PosOrderViewHolder, position: Int) {
        val posOrder = posOrderList[position]
        holder.id.text = posOrder.id.toString()
        holder.name.text = posOrder.name
        holder.state.text = posOrder.state
        holder.state.setTextColor(if(posOrder.serverId!! > 0) Color.GREEN else Color.RED)
        holder.customer.text = posOrder.customer?.displayName
        holder.totalCost.text = "${OConstants.CURRENCY_SYMBOL}${posOrder.amountTotal}"
        holder.date.text = DateUtils.formatToYYDDMMHHMMSS(posOrder.orderDate)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(it, posOrder, position)
        }

        holder.menu.setOnClickListener(View.OnClickListener { view ->
            onMoreButtonClick(view, posOrder)
        })
    }

    override fun getItemCount(): Int {
        return if(::posOrderList.isInitialized)  posOrderList.size else return 0
    }

    fun getItem(position: Int): PosOrder {
        return posOrderList[position]
    }

    fun changeList(posOrderList: List<PosOrder>) {
        this.posOrderList = posOrderList
        notifyDataSetChanged()
    }

    private fun onMoreButtonClick(view: View, posOrder: PosOrder) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener { item ->
            onMoreButtonClickListener.onItemClick(view, posOrder, item)
            true
        }
        popupMenu.inflate(R.menu.menu_pos_order_options)
        popupMenu.show()
    }


}
