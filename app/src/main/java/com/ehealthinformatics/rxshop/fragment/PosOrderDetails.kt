
package com.ehealthinformatics.rxshop.fragment

import android.os.Bundle
import android.widget.*
import com.ehealthinformatics.RxShop

import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.odoorx.core.data.dao.PosOrderDao
import com.ehealthinformatics.odoorx.core.data.dto.PosOrder
import com.ehealthinformatics.odoorx.rxshop.data.adapter.SalesAdapter
import com.ehealthinformatics.odoorx.core.data.dao.PosOrderLineDao
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooCompatActivity
import com.ehealthinformatics.odoorx.core.data.dao.QueryFields

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
        posOrderDao = RxShop.getDao(PosOrderDao::class.java)
        posOrderLineDao = RxShop.getDao(PosOrderLineDao::class.java)
        setContentView(R.layout.layout_customer_order)
        var posOrderLineId = intent.getIntExtra("pos_order_id", 0)
        setupView()
        loadPosOrder(posOrderLineId)
    }

    private fun loadPosOrder(posOrderId: Int){
        var posOrder = posOrderDao.get(posOrderId, QueryFields.all())
        var posOrderLines = posOrderLineDao.fromPosOrder(posOrder, QueryFields.all())
        posOrder.lines.addAll(posOrderLines)
        posOrder.setupValues()
    }

    private fun setupView() {
        tvPosName = findViewById(R.id.tv_pos_order_title) as TextView
       // tvStaffName = findViewById(R.id.tv_staff_name) as TextView
        tvCustomerName = findViewById(R.id.tv_customer_name) as TextView
        lvSalesList = findViewById(R.id.lv_sales_list) as ListView
      //  tvSalesTotal = findViewById(R.id.tv_sales_total) as TextView
        tvSalesTaxed = findViewById(R.id.tv_taxed_amount) as TextView
        tvSalesUnTaxed = findViewById(R.id.tv_untaxed_amount) as TextView
    }

    private fun PosOrder.setupValues() {
        orderLinesAdapter = SalesAdapter(this@PosOrderDetails, lines)
        lvSalesList?.adapter = orderLinesAdapter
        tvPosName?.text = name
        tvStaffName?.text = session!!.user!!.partner!!.displayName
        tvCustomerName?.text = customer!!.displayName

        tvSalesTotal?.text = amountTotal.toString()
        tvStaffName?.text = session!!.user!!.partner?.displayName
        tvSalesTaxed?.text = amountTax.toString()
    }

}