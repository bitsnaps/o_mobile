package com.odoo.rxshop.viewholder

import android.view.View
import android.widget.*

import com.odoo.odoorx.rxshop.R

class PurchaseOrderViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    public var name: TextView = itemView.findViewById(R.id.tv_purchase_order_name) as TextView
    public var vendor: TextView = itemView.findViewById(R.id.tv_purchase_order_vendor) as TextView
    public var state: TextView = itemView.findViewById(R.id.tv_purchase_order_state) as TextView
//    public var posOrderLines: ListView = itemView.findViewById(R.id.lv_sales_list) as ListView
    public var totalCost: TextView = itemView.findViewById(R.id.tv_purchase_order_total) as TextView
    public var menu: ImageButton = itemView.findViewById(R.id.ib_purchase_order_menu) as ImageButton
//    public var amountPaid: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountTaxInc: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountReturn: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var defaultCode: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var code: TextView = itemView.findViewById(R.id.tv_customer_name) as TextView

}