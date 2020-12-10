package com.odoo.rxshop.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.odoo.RxShop
import com.odoo.odoorx.core.base.auth.*
import com.odoo.odoorx.core.base.rpc.listeners.OdooError
import com.odoo.odoorx.core.base.support.OUser
import com.odoo.odoorx.core.data.dao.ProductDao
import com.odoo.odoorx.core.data.dao.QueryFields
import com.odoo.odoorx.rxshop.R
import com.odoo.rxshop.utils.LoadingUtils
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
        userAccount.authenticate()
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

    override fun onStartConfigLoad(): ISyncConfig {
        displayMessage("Initializing Daos...")
        RxShop.initDaos(userAccount)
        displayMessage("Loading Daos...")
        val artifactsLoader = LoadingUtils.ArtifactsLoader(userAccount)
        val syncConfig = artifactsLoader.load()
        displayMessage("Loading Products...")
        val productDao = RxShop.getDao<ProductDao>(ProductDao::class.java)
        productDao.selectAll(QueryFields.all())
        displayMessage("Finished Loading Products...")
        return syncConfig
    }

    override fun onConfigLoadSuccess(syncConfig: ISyncConfig?) {
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
