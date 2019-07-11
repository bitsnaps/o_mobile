
package com.odoo.addons.abirex.form

import android.os.Bundle
import android.widget.*

import com.odoo.R
import com.odoo.base.addons.abirex.dao.PosOrderDao
import com.odoo.base.addons.abirex.model.PosOrder
import com.odoo.base.addons.abirex.adapter.SalesAdapter
import com.odoo.core.support.OdooCompatActivity

class PosOrderDetails : OdooCompatActivity() {
    private var posOrderDao: PosOrderDao? = null
    private var tvPosName: TextView? = null
    private var tvStaffName: TextView? = null
    private var tvCustomerName: TextView? = null
    private var lvSalesList: ListView? = null
    private var tvSalesTotal: TextView? = null
    private var orderLinesAdapter: ListAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_point_of_sale)
        var posOrderLineId: Int = savedInstanceState!!.getInt("pos_order_id")
        setupView()
        loadPosOrder(posOrderLineId)
    }

    private fun loadPosOrder(posOrderId: Int){
        var posOrder = posOrderDao!!.get(posOrderId)
        var posOrderLines = posOrderDao!!.getOrderLines(posOrderId)
        posOrder.lines = posOrderLines
        posOrder.setupValues()
    }

    private fun setupView() {
        tvPosName = findViewById(R.id.tv_pos_name) as TextView
        tvStaffName = findViewById(R.id.tv_staff_name) as TextView
        tvCustomerName = findViewById(R.id.tv_customer_name) as TextView
        lvSalesList = findViewById(R.id.lv_sales_list) as ListView
        tvSalesTotal = findViewById(R.id.tv_sales_total) as TextView
    }

    private fun PosOrder.setupValues() {
        posOrderDao = PosOrderDao(applicationContext, null!!)
        orderLinesAdapter = SalesAdapter(this@PosOrderDetails, lines)
        lvSalesList?.adapter = orderLinesAdapter
        tvPosName?.text = name
        tvStaffName?.text = session.user.name
        tvCustomerName?.text = customer.name
    }


}