package com.odoo.addons.abirex.viewholder

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.odoo.App

import com.odoo.R
import com.odoo.addons.abirex.form.PosOrderDetails
import com.odoo.core.utils.IntentUtils

class PosOrderViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

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

    override fun onClick(v: View?) {
        val posOrderId = adapterPosition
        val data = Bundle()
        if (posOrderId != null) {
            data.putInt("pos_order_id", posOrderId)
        }
        IntentUtils.startActivity(App.getContext(), PosOrderDetails::class.java, data)
    }

}