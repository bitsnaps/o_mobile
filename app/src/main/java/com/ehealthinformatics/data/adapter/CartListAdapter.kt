package com.ehealthinformatics.data.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ehealthinformatics.App

import com.ehealthinformatics.R
import com.ehealthinformatics.app.listeners.OnItemClickListener
import com.ehealthinformatics.data.dao.PosOrderLineDao
import com.ehealthinformatics.app.viewholder.CartOrderLineViewHolder
import com.ehealthinformatics.config.OConstants
import com.ehealthinformatics.data.dto.PosOrder
import com.ehealthinformatics.data.dto.PosOrderLine

class CartListAdapter constructor(private var onItemClick: OnItemClickListener<PosOrderLine>, var editMode: Boolean) : RecyclerView.Adapter<CartOrderLineViewHolder>() {

    lateinit var posOrder: PosOrder
    lateinit var posOrderLineDao: PosOrderLineDao
    lateinit var itemView: ViewGroup


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartOrderLineViewHolder {
        posOrderLineDao = App.getDao(PosOrderLineDao::class.java)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_cart_item,  parent,false)
        return CartOrderLineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartOrderLineViewHolder, position: Int) {
            val posOrderLine = posOrder.lines[position]
            holder.name.text = posOrderLine.product!!.name
            holder.desc.text = posOrderLine.product!!.productTemplate.uom?.name
            holder.price.text = "₦ %.2f".format(posOrderLine.unitPrice!! * posOrderLine!!.quantity!!)
            holder.qty.text = posOrderLine.quantity.toString()
            if(editMode && posOrder.state == "draft") {
                holder.add.setOnClickListener {
                    posOrderLine.quantity = posOrderLine.quantity!! + 1
                    posOrderLine.subTotalWithoutTax = posOrderLine.quantity!! * posOrderLine.unitPrice!!
                    posOrderLine.order!!.recalculate()
                    holder.qty.text = posOrderLine.quantity.toString()
                    holder.price.text = "₦ %.2f".format(posOrderLine.subTotalWithoutTax)
                    posOrderLineDao.update(posOrderLine.id!!, posOrderLine.toOValues())
                    posOrderLineDao.posOrderDao.update(posOrder.id!!, posOrder.toOValues())
                    onItemClick.onItemClick(it, posOrderLine, position)
                }

                holder.remove.setOnClickListener {
                    if (posOrderLine.quantity!! > 1F) {
                        posOrderLine.quantity = posOrderLine.quantity!! - 1
                        posOrderLine.subTotalWithoutTax = posOrderLine.quantity!! * posOrderLine.unitPrice!!
                        posOrderLine.order!!.recalculate()
                        holder.qty.text = posOrderLine.quantity.toString()
                        holder.price.text = OConstants.CURRENCY_SYMBOL + posOrderLine.subTotalWithoutTax.toString()
                        posOrderLineDao.update(posOrderLine.id!!, posOrderLine.toOValues())
                        posOrderLineDao.posOrderDao.update(posOrder.id!!, posOrder.toOValues())
                        onItemClick.onItemClick(it, posOrderLine, position)
                    }
                }
            } else {
                holder.add.visibility = View.INVISIBLE
                holder.remove.visibility = View.INVISIBLE
            }
    }

    override fun getItemCount(): Int {
        return if(::posOrder.isInitialized) posOrder.lines.size else 0
    }

    fun getItem(position: Int): PosOrderLine {
        return posOrder.lines[position]
    }

    fun changeOrder(posOrder: PosOrder){
        this.posOrder = posOrder
        notifyDataSetChanged()
    }

}
