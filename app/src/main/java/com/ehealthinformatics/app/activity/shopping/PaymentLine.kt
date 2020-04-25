package com.ehealthinformatics.app.activity.shopping

import android.app.ProgressDialog
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

import androidx.appcompat.widget.Toolbar

import com.ehealthinformatics.App
import com.ehealthinformatics.R
import com.ehealthinformatics.app.utils.Tools
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.data.dao.*
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.dto.*

import android.widget.Toast
import com.ehealthinformatics.core.utils.IntentUtils
import kotlinx.android.synthetic.main.fragment_payment_line.*
import java.lang.Float

class PaymentLine : OdooCompatActivity() {

    private var notCorrected = true
    private var paymentLinesActive  = false
    private var accountBankStatementId = 0
    private lateinit var posOrder: PosOrder
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var accountBankStatementDao: AccountBankStatementDao
    private lateinit var accountBankStatementLineDao: AccountBankStatementLineDao
    private lateinit var accountBankStatement: AccountBankStatement
    private lateinit var accountBankStatementLine: AccountBankStatementLine
    private var orderId = 0
    private lateinit var loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loading = ProgressDialog.show(this, "",
        "Loading. Please wait...", true)
        setContentView(R.layout.fragment_payment_line)
        initIntentParams()
        initToolbar()
        initDaos()
        initData(orderId)
    }

    private fun initIntentParams(){
        orderId = intent.getIntExtra(Columns.PosOrderLine.order_id, 0)
        paymentLinesActive =  intent.getBooleanExtra("PAYMENT_LINES", false)
        accountBankStatementId = intent.getIntExtra(Columns.AccountBankStatementLine.statement_id, 0)
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
        tv_payment_line_currency.text = "₦ "
        et_payment_line_amount.setText("%.2f".format(posOrder.calculateBalance()))
        et_payment_line_balance.text = "₦ %.2f".format(0.0)
        et_payment_line_real_amount.text = "₦ %.2f".format(posOrder.amountTotal)
        et_payment_line_balance.visibility = View.INVISIBLE
        var button: Button?
        for (i in 0 until et_payment_line_digits.childCount) {
            if (et_payment_line_digits.getChildAt(i) is LinearLayout) {
                var layoutRow = et_payment_line_digits.getChildAt(i) as LinearLayout
                for (i in 0 until layoutRow.childCount) {
                    if (layoutRow.getChildAt(i) is Button) {
                        button = layoutRow.getChildAt(i) as Button
                        button.setOnClickListener {
                            var text = (it as Button).text.toString()
                            if (text != "C")
                                appendInput(text)
                            else
                                correctInput()
                        }
                    }
                }
            }
        }
        btnNext.text = "Charge: ₦ %.2f".format(posOrder.calculateBalance())
        btnNext.setOnClickListener { makePayment() }
        redisplay("%.2f".format(posOrder.calculateBalance()))
    }

    //TODO: Add commas
    private fun appendInput (number: String) {
        notCorrected = false
        var paymentAmount = et_payment_line_amount.text.toString()
        if (paymentAmount.length == 11) { return }
        paymentAmount = paymentAmount.replace(".", "")
        if (paymentAmount.startsWith("0")) {
            paymentAmount = paymentAmount.removeRange(0, 1)
            paymentAmount += number
        }
        else {
            paymentAmount += number
        }
        var newLength = paymentAmount.length - 2
        val newPaymentAmount = paymentAmount.substring(0, newLength) + "." + paymentAmount.substring(newLength, paymentAmount.length)
        redisplay(newPaymentAmount)
    }

    private fun correctInput () {
        if(notCorrected) { et_payment_line_amount.setText("0.00"); }; notCorrected = false;
        var paymentAmount = et_payment_line_amount.text.toString().replace(".", "")
        var integerLength = paymentAmount.length - 2
        if(paymentAmount.length > 3){
            paymentAmount = paymentAmount.substring(0, paymentAmount.length - 1)
            integerLength -= 1
        } else {
            paymentAmount = paymentAmount.substring(0, paymentAmount.length - 1)
            paymentAmount = "0$paymentAmount"
        }
        var newLength = paymentAmount.length - 2
        val newPaymentAmount = paymentAmount.substring(0, newLength) + "." + paymentAmount.substring(newLength, paymentAmount.length)
        redisplay(newPaymentAmount)
    }

    private fun redisplay(paymentAmount:  String) {
        var amountAlreadyPaid = posOrder.calculateCreatedPaymentLinesTotal()
        var amountToBalance = posOrder.calculateBalance()
        var amountPaying =  Float.parseFloat(paymentAmount)
        var excessAmount = amountPaying - amountToBalance
        var debtAmount = amountToBalance - amountPaying
        var balanced = excessAmount == debtAmount


        if (excessAmount == 0F && debtAmount == 0F) { //Payment is Balanced
            accountBankStatementLine.amount = amountPaying
            posOrder.amountPaid = amountAlreadyPaid + amountPaying
        } else if (excessAmount > 0F && debtAmount < 0F) { //To give change
            accountBankStatementLine.amount = amountToBalance
            posOrder.amountPaid = amountAlreadyPaid + amountPaying
            posOrder.amountReturn = excessAmount
        } else if (excessAmount < 0F && debtAmount > 0F) { //Still owing
            accountBankStatementLine.amount = amountPaying
            posOrder.amountPaid = amountAlreadyPaid + amountPaying
        }

        et_payment_line_amount.setText("%.2f".format(amountPaying))
        et_payment_line_real_amount.visibility = if (balanced) {View.INVISIBLE} else View.VISIBLE
        et_payment_line_balance.visibility =  if (balanced) {View.INVISIBLE} else View.VISIBLE
        if (excessAmount > 0) {
            et_payment_line_balance.setTextColor(Color.GRAY)
            et_payment_line_balance.text =  "Change: ₦ %.2f".format(excessAmount)
        } else {
            et_payment_line_balance.setTextColor(Color.RED)
            et_payment_line_balance.text =  "Balance: ₦ %.2f".format(debtAmount)
        }
        btnNext.text =  if(excessAmount >= 0) { "Charge: ₦ %.2f".format(amountToBalance) } else "Next"
    }

    private fun initDaos(){
        accountBankStatementLineDao = App.getDao<AccountBankStatementLineDao>(AccountBankStatementLineDao::class.java)
        accountBankStatementDao = App.getDao<AccountBankStatementDao>(AccountBankStatementDao::class.java)
        posOrderDao = App.getDao<PosOrderDao>(PosOrderDao::class.java)
    }

    private fun initData(posOrderId: Int?) {
        loading.show()
        object : AsyncTask<Int, Void, PosOrder>() {

            override fun doInBackground(vararg posOrderIds: Int?): PosOrder {
                posOrder = posOrderDao.get(posOrderIds[0]!!, QueryFields.all())
                posOrder.accountBankStatements.addAll(accountBankStatementDao.posSessionStatements(posOrder.session!!))
                accountBankStatement = accountBankStatementDao.get(accountBankStatementId, QueryFields.all(), posOrder.session)
                accountBankStatementLine = createAccountBankStatementLine(posOrder, accountBankStatement, 0F)
                for (statement in posOrder.accountBankStatements) {
                    var lines = accountBankStatementLineDao.forStatement(posOrder, statement, QueryFields.all())
                    statement.statements.addAll(lines)
                    if(accountBankStatement.id === statement.id){
                        statement.statements.add(accountBankStatementLine)
                    }
                }

                return posOrder
            }

            override fun onPostExecute(posOrder: PosOrder) {
                super.onPostExecute(posOrder)
                this@PaymentLine.posOrder = posOrder
                initControls()
                ll_payment_line_controls.visibility = View.VISIBLE
                loading.hide()

            }
        }.execute(posOrderId)
    }

    private fun createAccountBankStatementLine(posOrder: PosOrder, accountBankStatement: AccountBankStatement, amount:  kotlin.Float): AccountBankStatementLine {
        return AccountBankStatementLine(posOrder, accountBankStatement, amount)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else {
            Toast.makeText(applicationContext, item.title, Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun makePayment() {
        loading.show()
            object : AsyncTask<Int, Int, Int>() {
                override fun doInBackground(vararg voids: Int?)  : Int? {

                    val  id = accountBankStatementLineDao.insert(accountBankStatementLine.toOValues())
                    accountBankStatementLine.id = id
                    if(id > 0) {
                        if(posOrder.calculateBalance() <= 0)
                        posOrder.state = Columns.PosOrder.State.PAID
                        posOrderDao.update(posOrder.id!!, posOrder.toOValues())
                    }
                    return id
                }

                override fun onPostExecute(void: Int) {
                    loading.hide()
                    val data = Bundle()
                    data.putInt(Columns.PosOrderLine.order_id, posOrder.id!!)
                    data.putInt(Columns.AccountBankStatementLine.statement_id, accountBankStatementId)
                    if (posOrder.calculateBalance() <= 0 && !paymentLinesActive) {
                        IntentUtils.startActivity(this@PaymentLine, PaymentSuccess::class.java, data)
                    } else {
                        IntentUtils.startActivity(this@PaymentLine, PaymentLines::class.java, data)
                    }
                }
            }.execute(0)
        }

}
