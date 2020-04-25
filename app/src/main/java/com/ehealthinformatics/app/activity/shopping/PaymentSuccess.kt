package com.ehealthinformatics.app.activity.shopping

import android.os.AsyncTask
import android.os.Bundle
import android.view.View

import com.ehealthinformatics.App
import com.ehealthinformatics.R
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.core.utils.IntentUtils
import com.ehealthinformatics.data.dao.*
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.dto.*

import kotlinx.android.synthetic.main.fragment_payment_success.*
import androidx.fragment.app.Fragment
import com.ehealthinformatics.app.fragment.PosOrderList


class PaymentSuccess : OdooCompatActivity() {

    private lateinit var posOrder: PosOrder
    private lateinit var posOrderDao: PosOrderDao
    private lateinit var accountBankStatementDao: AccountBankStatementDao
    private lateinit var accountBankStatementLineDao: AccountBankStatementLineDao
    private var orderId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment_success)
        initIntentParams()
        initDaos()
        initData(orderId)
        btn_payment_success_new_sale.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(IntentUtils.IntentParams.EDIT_MODE, true)
            finish();
            IntentUtils.startActivity(this@PaymentSuccess, PosOrderCart::class.java, bundle)

//            var mFragment: Fragment? = null
//            mFragment = PosOrderList()
//            val fragmentManager = supportFragmentManager
//            fragmentManager.beginTransaction().replace(R.id.frame_container, mFragment).commit()
        }
    }

    private fun initIntentParams(){
        orderId = intent.getIntExtra(Columns.PosOrderLine.order_id, 0)
    }

    private fun initDaos(){
        accountBankStatementLineDao = App.getDao<AccountBankStatementLineDao>(AccountBankStatementLineDao::class.java)
        accountBankStatementDao = App.getDao<AccountBankStatementDao>(AccountBankStatementDao::class.java)
        posOrderDao = App.getDao<PosOrderDao>(PosOrderDao::class.java)
    }

    private fun initData(posOrderId: Int?) {
        object : AsyncTask<Int, Void, PosOrder>() {

            override fun doInBackground(vararg posOrderIds: Int?): PosOrder {
                posOrder = posOrderDao.get(posOrderIds[0]!!, QueryFields.all())
                return posOrder
            }

            override fun onPostExecute(posOrder: PosOrder) {
                super.onPostExecute(posOrder)
                this@PaymentSuccess.posOrder = posOrder
                et_payment_success_amount.text = "₦ %.2f".format(posOrder.amountPaid!! - posOrder.amountReturn!!)
                et_payment_success_change.text = if (posOrder.amountReturn!! > 0F) { "Change: ₦ %.2f".format(posOrder.amountReturn) } else { "" }
                ll_payment_success_loading.visibility = View.INVISIBLE
                ll_payment_success_success.visibility = View.VISIBLE
            }
        }.execute(posOrderId)

    }

    fun doNothing() {}

    override fun onBackPressed() {
        doNothing()
    }


}
