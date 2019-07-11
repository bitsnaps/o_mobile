package com.odoo.addons.abirex.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*

import com.odoo.R

class PosOrderViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    public var name: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var sessionName: TextView = itemView.findViewById(R.id.tv_product_type) as TextView
    public var customerName: TextView = itemView.findViewById(R.id.tv_product_rating_no) as TextView
//    public var posOrderLines: ListView = itemView.findViewById(R.id.lv_sales_list) as ListView
    public var reference: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var totalAmount: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var amountPaid: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var amountTaxInc: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var amountReturn: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var defaultCode: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var code: TextView = itemView.findViewById(R.id.tv_customer_name) as TextView

}