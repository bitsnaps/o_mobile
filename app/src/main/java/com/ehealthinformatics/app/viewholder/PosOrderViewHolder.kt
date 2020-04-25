package com.ehealthinformatics.app.viewholder

import android.os.Bundle
import android.view.View
import android.widget.*

import com.ehealthinformatics.R

class PosOrderViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    public var id: TextView = itemView.findViewById(R.id.tv_product_id) as TextView
    public var name: TextView = itemView.findViewById(R.id.tv_pos_order_name) as TextView
    public var customer: TextView = itemView.findViewById(R.id.tv_pos_order_customer) as TextView
    public var state: TextView = itemView.findViewById(R.id.tv_pos_order_state) as TextView
//    public var posOrderLines: ListView = itemView.findViewById(R.id.lv_sales_list) as ListView
    public var date: TextView = itemView.findViewById(R.id.tv_pos_order_date) as TextView
    public var totalCost: TextView = itemView.findViewById(R.id.tv_pos_order_total) as TextView
    public var menu: ImageButton = itemView.findViewById(R.id.ib_pos_order_menu) as ImageButton
//    public var amountPaid: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountTaxInc: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountReturn: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var defaultCode: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var code: TextView = itemView.findViewById(R.id.tv_customer_name) as TextView

    override fun onClick(v: View?) {
        val posOrderId =  Integer.parseInt(id.text.toString())
        val data = Bundle()
        data.putInt("pos_order_id", posOrderId)

        //IntentUtils.startActivity(App.getContext(), PosOrderDetails::class.java, data)
    }

}