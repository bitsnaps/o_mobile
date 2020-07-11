package com.ehealthinformatics.rxshop.activity.shopping

import android.os.AsyncTask
import android.os.Bundle
import android.view.View

import com.ehealthinformatics.RxShop
import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.odoorx.rxshop.base.support.OdooCompatActivity
import com.ehealthinformatics.odoorx.core.base.utils.IntentUtils
import com.ehealthinformatics.odoorx.core.data.dao.*
import com.ehealthinformatics.odoorx.core.data.db.Columns
import com.ehealthinformatics.odoorx.core.data.dto.*
import kotlinx.android.synthetic.main.fragment_payment_success.*


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
