
package com.odoo.addons.abirex.form

import android.content.Context
import android.os.Bundle
import android.widget.*
import com.klinker.android.send_message.Message
import com.klinker.android.send_message.Settings
import com.klinker.android.send_message.Transaction
import com.odoo.App

import com.odoo.R
import com.odoo.base.addons.abirex.adapter.ProductSugestionsAdapter
import com.odoo.base.addons.abirex.adapter.SalesAdapter
import com.odoo.base.addons.abirex.dao.PosOrderDao
import com.odoo.base.addons.abirex.dao.PosOrderLineDao
import com.odoo.base.addons.abirex.dao.ProductDao
import com.odoo.base.addons.abirex.dto.*
import com.odoo.core.support.OdooCompatActivity
import com.odoo.sms.SmsReceiver

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

        btnSendOffline!!.setOnClickListener { view ->
            posOrder.sendOffline()
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
        tvStaffName?.text = session.user.partner.displayName
        tvCustomerName?.text = customer.displayName
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
        val sendSettings = Settings()
        sendSettings.setUseSystemSending(true)
        val transaction =  Transaction(applicationContext, sendSettings)
        val message =  Message(toSMS(), "+2348065701493")
        //TODO: Change to thread
        transaction.sendNewMessage(message, 9)
    }

    private fun PosOrder.addProduct(product: Product){
        var total = product.price * 1F;
        var posOrderLine = PosOrderLine(0, product.name,  App.getCompany()  , product ,
                "Thanks", product.price, 1F, total, total, 0F, this)
        posOrderLine.id = posOrderLineDao.insert(posOrderLine.toOValues())
        posOrder.addNewOrderLine(posOrderLine)
        posOrderLinesAdapter?.notifyDataSetChanged()
        recalculate()
    }

    private fun PosOrder.recalculate(){
        val oldAmountTax = amountTax
        val oldAmountTotal = amountTotal
        for(orderLine in lines){
            amountTax += orderLine.subTotalWithTax
            amountTotal += orderLine.subTotalWithoutTax
        }
        if(posOrderDao.update(id, toOValues())){
            setUIValues()
        } else {
            amountTax = oldAmountTax
            amountTotal = oldAmountTotal
        }
    }

}