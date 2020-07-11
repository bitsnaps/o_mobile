package com.ehealthinformatics.rxshop.viewholder

import android.view.View
import android.widget.*

import com.ehealthinformatics.odoorx.rxshop.R

class PartnerViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    public var name: TextView = itemView.findViewById(R.id.name) as TextView
    public var  companyName: TextView = itemView.findViewById(R.id.company_name) as TextView
    public var email: TextView = itemView.findViewById(R.id.email) as TextView
//    public var customer: TextView = itemView.findViewById(R.id.tv_pos_order_customer) as TextView
//    public var state: TextView = itemView.findViewById(R.id.tv_pos_order_state) as TextView
//    public var posOrderLines: ListView = itemView.findViewById(R.id.lv_sales_list) as ListView
//    public var totalCost: TextView = itemView.findViewById(R.id.tv_pos_order_total) as TextView
    public var imageSmall: ImageView = itemView.findViewById(R.id.image_small) as ImageView
//    public var amountPaid: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountTaxInc: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountReturn: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var defaultCode: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var code: TextView = itemView.findViewById(R.id.tv_customer_name) as TextView

    override fun onClick(v: View?) {


        //IntentUtils.startActivity(App.getContext(), PosOrderDetails::class.java, data)
    }

}