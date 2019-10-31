
package com.odoo.addons.abirex.detail

import android.os.Bundle
import android.widget.*
import com.odoo.App

import com.odoo.R
import com.odoo.base.addons.abirex.dao.PosOrderDao
import com.odoo.base.addons.abirex.dto.PosOrder
import com.odoo.base.addons.abirex.adapter.SalesAdapter
import com.odoo.base.addons.abirex.dao.PosOrderLineDao
import com.odoo.core.support.OdooCompatActivity

class PosOrderDetails : OdooCompatActivity() {
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var posOrderLineDao: PosOrderLineDao
    private var tvPosName: TextView? = null
    private var tvStaffName: TextView? = null
    private var tvCustomerName: TextView? = null
    private var lvSalesList: ListView? = null
    private var tvSalesTotal: TextView? = null
    private var tvSalesTaxed: TextView? = null
    private var tvSalesUnTaxed: TextView? = null
    private var orderLinesAdapter: ListAdapter? = null
    private var productSuggestionAdapter: ListAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posOrderDao = App.getDao(PosOrderDao::class.java)
        posOrderLineDao = App.getDao(PosOrderLineDao::class.java)
        setContentView(R.layout.layout_point_of_sale)
        var posOrderLineId = intent.getIntExtra("pos_order_id", 0)
        setupView()
        loadPosOrder(posOrderLineId)
    }

    private fun loadPosOrder(posOrderId: Int){
        var posOrder = posOrderDao.get(posOrderId)
        var posOrderLines = posOrderLineDao.fromPosOrder(posOrder)
        posOrder.lines.addAll(posOrderLines)
        posOrder.setupValues()
    }

    private fun setupView() {
        tvPosName = findViewById(R.id.tv_pos_order_title) as TextView
        tvStaffName = findViewById(R.id.tv_staff_name) as TextView
        tvCustomerName = findViewById(R.id.tv_customer_name) as TextView
        lvSalesList = findViewById(R.id.lv_sales_list) as ListView
        tvSalesTotal = findViewById(R.id.tv_sales_total) as TextView
        tvSalesTaxed = findViewById(R.id.tv_taxed_amount) as TextView
        tvSalesUnTaxed = findViewById(R.id.tv_untaxed_amount) as TextView
    }

    private fun PosOrder.setupValues() {
        orderLinesAdapter = SalesAdapter(this@PosOrderDetails, lines)
        lvSalesList?.adapter = orderLinesAdapter
        tvPosName?.text = name
        tvStaffName?.text = session.user.partner.displayName
        tvCustomerName?.text = customer.displayName

        tvSalesTotal?.text = amountTotal.toString()
        tvStaffName?.text = session.user.partner.displayName
        tvSalesTaxed?.text = amountTax.toString()
    }

}