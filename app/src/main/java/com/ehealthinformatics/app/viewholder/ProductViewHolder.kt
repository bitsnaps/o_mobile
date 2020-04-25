package com.ehealthinformatics.app.viewholder

import android.view.View
import android.widget.*

import com.ehealthinformatics.R
import odoo.controls.BezelImageView

class ProductViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView), View.OnClickListener {

    public var name: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
    public var icon: BezelImageView = itemView.findViewById(R.id.biv_product_icon) as BezelImageView
    public var uom: TextView = itemView.findViewById(R.id.tv_product_uom) as TextView
//    public var posOrderLines: ListView = itemView.findViewById(R.id.lv_sales_list) as ListView
    public var price: TextView = itemView.findViewById(R.id.tv_product_price) as TextView
//§    public var menu: ImageButton = itemView.findViewById(R.id.ib_customer_menu) as ImageButton
    public var category: TextView = itemView.findViewById(R.id.tv_product_category) as TextView
//    public var amountTaxInc: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var amountReturn: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var defaultCode: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
//    public var code: TextView = itemView.findViewById(R.id.tv_customer_name) as TextView

    override fun onClick(v: View?) {


        //IntentUtils.startActivity(App.getContext(), PosOrderDetails::class.java, data)
    }

}