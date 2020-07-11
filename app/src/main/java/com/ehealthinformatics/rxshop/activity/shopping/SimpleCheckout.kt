package com.ehealthinformatics.rxshop.activity.shopping

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*

import androidx.appcompat.widget.Toolbar

import com.ehealthinformatics.RxShop
import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.rxshop.utils.DialogUtils
import com.ehealthinformatics.rxshop.utils.Tools
import com.ehealthinformatics.odoorx.core.base.rpc.helper.ODomain
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooCompatActivity
import com.ehealthinformatics.odoorx.core.data.dao.*
import com.ehealthinformatics.odoorx.core.data.db.Columns
import com.ehealthinformatics.odoorx.core.data.dto.*
import kotlinx.android.synthetic.main.activity_form_checkout.*

class SimpleCheckout : OdooCompatActivity() {

    private lateinit var posOrder: PosOrder
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var posOrderLineDao: PosOrderLineDao
    private lateinit var accountBankStatementDao: AccountBankStatementDao
    private lateinit var accountBankStatementLineDao: AccountBankStatementLineDao
    private var orderId = 0
    private lateinit var loading: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_checkout)
        loading = ProgressDialog.show(this, "",
                "Loading. Please wait...", true)
        initIntentParams()
        initToolbar()
        initDaos()
        initData(orderId)
        initListeners()
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

    private fun initDaos(){
        accountBankStatementLineDao = RxShop.getDao<AccountBankStatementLineDao>(AccountBankStatementLineDao::class.java)
        accountBankStatementDao = RxShop.getDao<AccountBankStatementDao>(AccountBankStatementDao::class.java)
        posOrderDao = RxShop.getDao<PosOrderDao>(PosOrderDao::class.java)
    }

    private fun initData(posOrderId: Int?) {
        loading.show()
        object : AsyncTask<Int, Void, PosOrder>() {

            override fun doInBackground(vararg posOrderIds: Int?): PosOrder {
                posOrder = posOrderDao.get(posOrderIds[0]!!, QueryFields.all())
                return posOrder
            }

            override fun onPostExecute(posOrder: PosOrder) {
                super.onPostExecute(posOrder)
                this@SimpleCheckout.posOrder = posOrder
                loading.hide()
                initControlData()
            }
        }.execute(posOrderId)

    }

    private fun initListeners() {
        btn_checkout_process.setOnClickListener { processOrder(posOrder) }
        et_checkout_payment_type.setOnClickListener { v -> showPaymentDialog(v) }
        act_checkout_customer.setOnClickListener {
            val searchIntent = Intent(this@SimpleCheckout, SearchCustomer::class.java)
            startActivityForResult(searchIntent, 1)
        }
    }

    private fun initControlData() {
        val customer = posOrder.customer
        act_checkout_customer.setText(customer!!.displayName)
        act_checkout_email.setText(customer?.email)
        act_checkout_phone.setText(customer?.phone)
        act_checkout_address.setText(customer?.address)
        act_checkout_total_cost.setText(posOrder.amountTotal.toString())
    }

    private fun createAccountBankStatementLine(posOrder: PosOrder, accountBankStatement: AccountBankStatement, line: Float): AccountBankStatementLine {
        return AccountBankStatementLine(posOrder, accountBankStatement, line)
    }

    private fun processOrder(posOrder: PosOrder) {
        loading.show()
        btn_checkout_process.text = "PROCESSING..."
        btn_checkout_process.isEnabled = false
        object : AsyncTask<PosOrder, Void, Boolean>() {
            override fun doInBackground(vararg posOrders: PosOrder): Boolean? {
                val updated = posOrderDao.update(posOrder.id!!, posOrder.toOValues())
                if (posOrder.serverId!! < 1) {
                    val oDomain =  ODomain()
                    oDomain.add(Columns.server_id, "=", posOrder.id)
                    posOrderDao.quickSyncRecords(oDomain)
                } else {
                    posOrderDao.quickCreateRecord(posOrder.toOValues().toDataRow())
                }

                return updated
            }

            override fun onPostExecute(updated: Boolean?) {
                loading.hide()
                if (updated!!) {
                    btn_checkout_process.text = "COMPLETED"
                    DialogUtils.showPaymentDialog(this@SimpleCheckout,true)
                } else {
                    DialogUtils.showPaymentDialog(this@SimpleCheckout,false)
                }
            }
        }.execute(posOrder)

    }

    private fun showPaymentDialog(v: View) {
        if(posOrder.session!!.config!!.paymentJournals.isNotEmpty()){
            val paymentStatements = posOrder!!.session!!.statements;
            var paymentNames = java.util.ArrayList<CharSequence>()
            for(paymentStatement in paymentStatements)paymentNames.add(paymentStatement.journal.name)
            val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, paymentNames)
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Payment Type")
            builder.setSingleChoiceItems(listAdapter, -1) { dialogInterface, i ->
                (v as EditText).setText(paymentStatements[i].name)
                makePayment(paymentStatements[i])
                dialogInterface.dismiss()
            }
            builder.show()
        } else {
            Toast.makeText(applicationContext, "No Payment methods Available", Toast.LENGTH_SHORT).show()
            loading.hide()
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

    private fun makePayment(accountBankStatement: AccountBankStatement) {
        loading.show()
        var paymentSuccessful = false
        paymentSuccessful = true
        btn_checkout_process.isEnabled = true

        if (paymentSuccessful) {
            posOrder.amountPaid = posOrder.amountTotal
            posOrder.state = Columns.PosOrder.State.PAID
            act_checkout_total_paid.setText(posOrder.amountTotal.toString())
            val accountBankStatementLine = createAccountBankStatementLine(posOrder, accountBankStatement, posOrder.amountPaid!!.toFloat())
            val id = accountBankStatementLineDao.insert(accountBankStatementLine.toOValues())
            accountBankStatementLine.id = id
            object : AsyncTask<Int, Int, Int>() {
                override fun doInBackground(vararg voids: Int?)  : Int? {
                    //TODO: This can be better
                    val oDataRow = accountBankStatementLine.toOValues().toDataRow()
                    oDataRow.put(Columns.id, accountBankStatementLine.id)
                    oDataRow.put(Columns.server_id, accountBankStatementLine.serverId)
                    oDataRow.put(Columns.AccountBankStatementLine.currency_id, null)
                    val row = posOrder.toOValues().toDataRow();
                    row.put(Columns.id, posOrder.id)
                    val posRow= posOrderDao.quickCreateRecord(row)
                    if(posRow.getInt(Columns.server_id) > 0)
                    posOrder.serverId = posRow.getInt(Columns.server_id)
                    val createdRow = accountBankStatementLineDao.quickCreateRecord(oDataRow)
                    //TODO: Handle to check if created
                    if(createdRow.getInt(Columns.server_id) > 0){}
                    return 0
                }

                override fun onPostExecute(void: Int) {
                    loading.hide()
                }
            }.execute(0)
        }
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
                    act_checkout_customer.setText(customer.displayName)
                }
            }
        }
    }

}
