package com.ehealthinformatics.rxshop.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.ehealthinformatics.RxShop
import com.ehealthinformatics.odoorx.core.base.auth.IConfigLoadListener
import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.rxshop.utils.LoadingUtils
import com.ehealthinformatics.odoorx.core.base.auth.ILoginProgressStatus
import com.ehealthinformatics.odoorx.core.base.auth.LoginProgressStatus
import com.ehealthinformatics.odoorx.core.base.auth.OUserAccount
import com.ehealthinformatics.odoorx.core.base.rpc.listeners.OdooError
import com.ehealthinformatics.odoorx.core.base.support.OUser
import com.ehealthinformatics.odoorx.core.data.dao.ProductDao
import com.ehealthinformatics.odoorx.core.data.dao.QueryFields
import com.ehealthinformatics.odoorx.core.data.dto.SyncConfig
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() , ILoginProgressStatus, IConfigLoadListener {
    lateinit var userAccount: OUserAccount
    var loginProgressStatus = LoginProgressStatus.INIT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val mUser = OUser.current(this)
        if (mUser != null) {
            userAccount = OUserAccount.getInstance(RxShop.getAppContext(), mUser, this@SplashScreenActivity, this@SplashScreenActivity)
        } else {
            startOdooLogin()
            finish()
        }
    }

    override fun onConnect(databases: MutableList<String>?) {
        displayMessage("Connected to Odoo...")
    }

    override fun onLoginSuccess(oUser: OUser?) {
        tv_progress_status.text = "Login successful..."
    }

    override fun onConnectionError(error: OdooError?) {
        displayMessage("Error connecting to odoo")
    }

    private fun startOdooActivity() {
        startActivity(Intent(this, OdooActivity::class.java))
        finish()
    }

    private fun startOdooLogin() {
        startActivity(Intent(this, OdooLogin::class.java))
        finish()
    }

    override fun onStartConfigLoad(): SyncConfig {
        displayMessage("Loading configurations...")
        val artifactsLoader = LoadingUtils.ArtifactsLoader(userAccount)
        val syncConfig = artifactsLoader.load()
        val productDao = RxShop.getDao<ProductDao>(ProductDao::class.java)
        productDao.selectAll(QueryFields.all())
        return syncConfig
    }

    override fun onConfigLoadSuccess(syncConfig: SyncConfig?) {
       displayMessage("Loaded configuration Succesfully")
        startOdooActivity()
    }

    override fun onConfigLoadError(e: OdooError?) {
        displayMessage("Error Loading Configuration...")
    }

    private fun displayMessage(message: String) {
        runOnUiThread {
            tv_progress_status.text = message
        }
    }

}
