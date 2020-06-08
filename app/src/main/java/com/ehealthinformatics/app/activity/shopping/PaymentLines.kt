package com.ehealthinformatics.app.activity.shopping

import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*

import androidx.appcompat.widget.Toolbar

import com.ehealthinformatics.App
import com.ehealthinformatics.R
import com.ehealthinformatics.app.utils.Tools
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.data.dao.*
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.dto.*

import kotlinx.android.synthetic.main.fragment_payment_line.et_payment_line_balance
import kotlinx.android.synthetic.main.fragment_payment_lines.*
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.content.Context
import com.ehealthinformatics.app.listeners.OnItemClickListener
import com.ehealthinformatics.app.utils.DialogUtils
import com.ehealthinformatics.core.utils.IntentUtils

class PaymentLines : OdooCompatActivity() {

    private lateinit var posOrder: PosOrder
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var accountBankStatementDao: AccountBankStatementDao
    private lateinit var accountBankStatementLineDao: AccountBankStatementLineDao
    private var orderId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment_lines)
        initIntentParams()
        initToolbar()
        initDaos()
        initData(orderId)
    }

    private fun initIntentParams(){
        orderId = intent.getIntExtra(Columns.PosOrderLine.order_id, 0)
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Checkout"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this)
    }

    //TODO: Add feature of multiple payment lines
    private fun initControls () {
        et_payment_line_amount_due.text = "₦ %.2f".format(posOrder.amountTotal)
        var paymentList = PaymentAdapter(this, OnItemClickListener { v, item, pos ->

        })
        lv_payment_line_items.adapter = paymentList
        var balance = posOrder.calculateBalance()
        et_payment_line_balance.text = "₦ %.2f".format( balance)
        et_payment_line_balance.visibility = if(balance == 0F) { View.INVISIBLE } else { View.VISIBLE }
        btn_next.text = if(balance > 0){ "Add Payment" } else {"Charge ₦ %.2f".format(posOrder.amountPaid!! - posOrder.amountReturn!!) }
        btn_next.setOnClickListener {
            if (balance > 0) {
                showPaymentTypesDialog()
            } else {
                showSuccess()
            }
        }
    }

    private inner class PaymentAdapter(context: Context, var removeListener: OnItemClickListener<AccountBankStatementLine>) : ArrayAdapter<AccountBankStatementLine>(context,R.layout.list_item_payment, posOrder.getStatementLines() ) {

        override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
            var vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v = vi.inflate(R.layout.list_item_payment, null)
            val accountBankStatementLine = getItem(pos);
            v.findViewById<TextView>(R.id.iv_payment_item_name).text = accountBankStatementLine.name
            v.findViewById<TextView>(R.id.iv_payment_item_amount).text = "₦ %.2f".format(accountBankStatementLine.amount)
            v.findViewById<ImageView>(R.id.iv_payment_item_cancel).setOnClickListener {
               removeListener.onItemClick(v, accountBankStatementLine, pos)
            }
            return v
        }
    }

    private fun initDaos() {
        accountBankStatementLineDao = App.getDao<AccountBankStatementLineDao>(AccountBankStatementLineDao::class.java)
        accountBankStatementDao = App.getDao<AccountBankStatementDao>(AccountBankStatementDao::class.java)
        posOrderDao = App.getDao<PosOrderDao>(PosOrderDao::class.java)
    }

    private fun initData(posOrderId: Int?) {
        object : AsyncTask<Int, Void, PosOrder>() {

            override fun doInBackground(vararg posOrderIds: Int?): PosOrder {
                posOrder = posOrderDao.get(posOrderIds[0]!!, QueryFields.all())
                posOrder.accountBankStatements.addAll(accountBankStatementDao.posSessionStatements(posOrder.session!!))
                for (statement in posOrder.accountBankStatements) {
                    statement.statements.addAll(accountBankStatementLineDao.forStatement(posOrder, statement, QueryFields.all()))
                }
                return posOrder
            }

            override fun onPostExecute(posOrder: PosOrder) {
                super.onPostExecute(posOrder)
                this@PaymentLines.posOrder = posOrder
                initControls()
                ll_payment_lines_controls.visibility = View.VISIBLE
                ll_payment_lines_loading.visibility = View.GONE

            }
        }.execute(posOrderId)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPaymentTypesDialog() {
       DialogUtils.showPaymentTypeDialog(this, posOrder)

    }

    private fun showSuccess() {
        val data = Bundle()
        data.putInt(Columns.PosOrderLine.order_id, posOrder.id!!)
        IntentUtils.startActivity(this, PaymentSuccess::class.java, data)
    }

}
