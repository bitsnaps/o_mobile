
package com.ehealthinformatics.app.activity.pos

import android.os.Bundle
import android.widget.*
import com.ehealthinformatics.App

import com.ehealthinformatics.R
import com.ehealthinformatics.app.activity.shopping.PosOrderCart
import com.ehealthinformatics.data.adapter.ProductSugestionsAdapter
import com.ehealthinformatics.data.adapter.SalesAdapter
import com.ehealthinformatics.data.dao.PosOrderDao
import com.ehealthinformatics.data.dao.PosOrderLineDao
import com.ehealthinformatics.data.dao.ProductDao
import com.ehealthinformatics.data.dto.*
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.core.utils.IntentUtils

class PosOrderEdit : OdooCompatActivity() {

    private lateinit var posOrder : PosOrder

    private lateinit var posOrderDao: PosOrderDao
    private lateinit var posOrderLineDao: PosOrderLineDao
    private var tvSearchProduct: AutoCompleteTextView? = null
    private var tvPosName: TextView? = null
    private var tvSyncStatus: TextView? = null
    private var tvStaffName: TextView? = null
    private var tvCustomerName: TextView? = null
    private var lvSalesList: ListView? = null
    private var tvSalesTotal: TextView? = null
    private var tvSalesTaxed: TextView? = null
    private var tvSalesUnTaxed: TextView? = null
    private var btnSendOffline: Button? = null
    private var btnPay: Button? = null
    private var btnClear: Button? = null
    private var productSugestionsAdapter: ProductSugestionsAdapter? = null
    private var posOrderLinesAdapter: SalesAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        posOrderDao = App.getDao(PosOrderDao::class.java)
        posOrderLineDao = App.getDao(PosOrderLineDao::class.java)
        setContentView(R.layout.layout_point_of_sale_edit)
        initView()
        initActions()
        initVars()
    }

//    private fun loadPosOrder(posOrderId: Int){
//        var posOrder = posOrderDao.get(posOrderId)
//        var posOrderLines = posOrderLineDao.fromPosOrder(posOrderId)
//        posOrder.lines.  posOrderLines
//        posOrder.setUIValues()
//    }

    private fun initView() {
        tvPosName = findViewById(R.id.tv_pos_order_title) as TextView
        tvSyncStatus = findViewById(R.id.tv_sync_status) as TextView
        tvCustomerName = findViewById(R.id.tv_customer_name) as TextView
        lvSalesList = findViewById(R.id.lv_sales_list) as ListView
        tvSalesTaxed = findViewById(R.id.tv_taxed_amount) as TextView
        tvSalesUnTaxed = findViewById(R.id.tv_untaxed_amount) as TextView
        tvSalesTotal = findViewById(R.id.tv_total_amount) as TextView
        tvSearchProduct = findViewById(R.id.tv_search_item) as AutoCompleteTextView
        btnSendOffline = findViewById(R.id.btn_send_offline) as Button
        btnPay = findViewById(R.id.btn_pay) as Button
        btnClear = findViewById(R.id.btn_clear) as Button
    }

    private fun initActions() {
        tvSearchProduct!!.setOnItemClickListener { parent, view, position, id ->
            var product = productSugestionsAdapter!!.getItem(position)
            tvSearchProduct!!.text.clear()
            posOrder.addProduct(product)
        }

        btnSendOffline!!.setOnClickListener {
            posOrder.sendOffline()
        }

        btnPay!!.setOnClickListener {
            val data = Bundle()
            data.putInt("pos_order_id", posOrder.id!!)
            IntentUtils.startActivity(this, PosOrderCart::class.java, data)
        }
    }

    private fun initVars() {
        posOrder = posOrderDao.newOrder()
        posOrderLinesAdapter = SalesAdapter(this, posOrder.lines)
        productSugestionsAdapter = ProductSugestionsAdapter(this, App.getDao(ProductDao::class.java))
        posOrder.setUIValues()
    }

    private fun PosOrder.setUIValues() {
        //Top
        tvPosName?.text = name
        tvStaffName?.text = session!!.user!!.partner?.displayName
        tvCustomerName?.text = customer!!.displayName
        tvSearchProduct?.setAdapter(productSugestionsAdapter)
        //List
        lvSalesList?.adapter = posOrderLinesAdapter
        //Bottom
        tvSalesUnTaxed?.text = amountTotal.toString()
        tvSalesTaxed?.text = amountTax.toString()
        tvSalesTaxed?.text = amountTax.toString()
        tvSalesTaxed?.text = amountTax.toString()
    }

    private fun PosOrder.toSMS(): String {
        return encode2SMS()
    }

    private fun PosOrder.sendOffline() {
//        val sendSettings = Settings()
//        sendSettings.useSystemSending = true
//        val transaction =  Transaction(applicationContext, sendSettings)
//        val message =  Message(toSMS(), "+2348065701493")
//        //TODO: Change to thread
//        transaction.sendNewMessage(message, 9)
    }

    private fun PosOrder.addProductt(product: Product){
        var posOrderLine = addProduct(product)
        posOrderLine.id = posOrderLineDao.insert(posOrderLine.toOValues())
        posOrder.addNewOrderLine(posOrderLine)
        posOrderLinesAdapter?.notifyDataSetChanged()
        recalculateAndSave()
    }

    private fun PosOrder.recalculateAndSave(){
        recalculate()
        posOrderDao.update(id!!, toOValues())
        setUIValues()

    }

}