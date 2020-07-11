package com.ehealthinformatics.odoorx.rxshop.data.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu

import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.rxshop.listeners.OnItemClickListener
import com.ehealthinformatics.rxshop.viewholder.PartnerViewHolder
import com.ehealthinformatics.odoorx.core.data.dto.Customer

class CustomerListAdapter(private var context: Context, private val onItemClickListener: OnItemClickListener<Customer>) :
        RecyclerView.Adapter<PartnerViewHolder>() {

    private lateinit var  customerList: List<Customer>
    private var listStyle = true


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):   PartnerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_item_customer , null)
        return   PartnerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder:   PartnerViewHolder, position: Int) {
        val  customer =  customerList[position]
        holder.name.text =  customer.displayName
        //holder.companyName.text =  customer.company!!.name
        holder.email.text = customer.email

//        holder.state.text =  customer.state
//        holder.state.setTextColor(if( customer.serverId > 0) Color.GREEN else Color.RED)
//        holder.customer.text =  customer.customer.displayName
//        holder.totalCost.text = "₦ ${ customer.amountTotal}"
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(it,  customer, position)
        }

//        holder.menu.setOnClickListener(View.OnClickListener { view ->
//            if (onMoreButtonClickListener == null) return@OnClickListener
//            onMoreButtonClick(view,  customer)
//        })
    }

    override fun getItemCount(): Int {
        return if(:: customerList.isInitialized)   customerList.size else return 0
    }

    fun getItem(position: Int):   Customer {
        return  customerList[position]
    }

    fun reset( customerList: List<Customer>) {
        this. customerList =  customerList
        notifyDataSetChanged()
    }

    private fun onMoreButtonClick(view: View,  customer:   Customer) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.setOnMenuItemClickListener { item ->
            //onMoreButtonClickListener.onItemClick(view,  customer, item)
            true
        }
        popupMenu.inflate(R.menu.menu_pos_order_options)
        popupMenu.show()
    }

    interface OnMoreButtonClickListener {
        fun onItemClick(view: View, obj:   Customer, item: MenuItem)
    }

}
