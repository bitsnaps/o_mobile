package com.ehealthinformatics.rxshop.activity.shopping

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View

import androidx.appcompat.widget.Toolbar

import com.ehealthinformatics.RxShop
import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.rxshop.utils.Tools
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooCompatActivity
import com.ehealthinformatics.odoorx.core.data.dao.*
import com.ehealthinformatics.odoorx.core.data.dto.*

import android.widget.Toast
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils
import com.ehealthinformatics.odoorx.core.data.db.Columns
import kotlinx.android.synthetic.main.fragment_payment_type.*
import kotlinx.android.synthetic.main.layout_payment_types.*


class PaymentType : OdooCompatActivity() {

    private lateinit var posOrder: PosOrder
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var accountBankStatementDao: AccountBankStatementDao
    private lateinit var accountBankStatementLineDao: AccountBankStatementLineDao
    private var orderId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment_type)
        initIntentParams()
        initToolbar()
        initDaos()
        initData(orderId)
    }

    private fun initIntentParams(){
        orderId = intent.getIntExtra(Columns.PosOrderLine.order_id, 0);
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Checkout"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this)
    }

    private fun initDaos() {
        accountBankStatementLineDao = RxShop.getDao<AccountBankStatementLineDao>(AccountBankStatementLineDao::class.java)
        accountBankStatementDao = RxShop.getDao<AccountBankStatementDao>(AccountBankStatementDao::class.java)
        posOrderDao = RxShop.getDao<PosOrderDao>(PosOrderDao::class.java)
    }

    private fun initData(posOrderId: Int?) {
        object : AsyncTask<Int, Void, PosOrder>() {

            override fun doInBackground(vararg posOrderIds: Int?): PosOrder {
                posOrder = posOrderDao.get(posOrderIds[0]!!, QueryFields.all())
                return posOrder
            }

            override fun onPostExecute(posOrder: PosOrder) {
                super.onPostExecute(posOrder)
                this@PaymentType.posOrder = posOrder
                tv_payment_type_total_amount.text = String.format("₦ %.2f", posOrder.amountTotal)
                loadPaymentTypes()
                ll_payment_type_loading.visibility = View.INVISIBLE
                rl_payment_type_controls.visibility = View.VISIBLE
            }
        }.execute(posOrderId)

    }

    private fun loadPaymentTypes () {

        if (posOrder.session!!.config!!.paymentJournals.isNotEmpty()) {
            val paymentStatements = posOrder!!.session!!.statements;
            var paymentNames = java.util.ArrayList<CharSequence>()
            for(paymentStatement in paymentStatements)paymentNames.add(paymentStatement.journal.name)
            for((index, statement) in posOrder.session!!.statements.withIndex()){
                var buttonPayment = btnPaymentType0

                if(index == 0) {
                    buttonPayment = btnPaymentType0
                }
                if(index == 1) {
                    buttonPayment = btnPaymentType1
                }
                if(index == 2) {
                    buttonPayment = btnPaymentType2
                }
                if(index == 3) {
                    buttonPayment = btnPaymentType3
                }
                if(index == 4) {
                    buttonPayment = btnPaymentType4
                }
                if(index == 5) {
                    buttonPayment = btnPaymentType5
                }
                buttonPayment.text = statement.journal.name.substring(0, 3)
                buttonPayment.setOnClickListener {
                    val data = Bundle()
                    data.putInt(Columns.PosOrderLine.order_id, posOrder.id!!)
                    data.putInt(Columns.AccountBankStatementLine.statement_id, statement.id)
                    IntentUtils.startActivity(this, PaymentLine::class.java, data)
                }
            }

        } else {
            Toast.makeText(applicationContext, "No Payment methods Available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val customerId = data!!.getIntExtra(Columns.PosOrder.partner_id, 0)
                if (customerId > 0) {
                    val customer = Customer(posOrderDao!!.partnerDao.get(customerId, QueryFields.all()))
                    posOrder!!.customer = customer
                    posOrderDao!!.update(posOrder.id!!, posOrder!!.toOValues())
                    Toast.makeText(applicationContext, customer.displayName + " selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



}
