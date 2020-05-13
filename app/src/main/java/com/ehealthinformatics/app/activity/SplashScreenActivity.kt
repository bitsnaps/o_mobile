package com.ehealthinformatics.app.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler

import androidx.appcompat.app.AppCompatActivity
import com.ehealthinformatics.App
import com.ehealthinformatics.R
import com.ehealthinformatics.app.utils.LoadingUtils
import com.ehealthinformatics.core.support.OUser
import com.ehealthinformatics.data.dao.ProductDao
import com.ehealthinformatics.data.dao.QueryFields
import com.ehealthinformatics.data.dto.SyncConfig

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val mUser = OUser.current(this)
        if (mUser != null) {
            DaoInitializer().execute(mUser)
        } else {
            startOdooLogin()
        }
    }

    private fun startOdooActivity() {
        startActivity(Intent(this, OdooActivity::class.java))
        finish()
    }

    private fun startOdooLogin() {
        startActivity(Intent(this, OdooLogin::class.java))
        finish()
    }

    private inner class DaoInitializer : AsyncTask<OUser, String, Void?>() {

        private var mUser: OUser? = null

//        override fun onPreExecute() {
//            super.onPreExecute()
//            mLoginProcessStatus.setText("Syncing User Configuration")
//        }
//
//        override fun onProgressUpdate(vararg values: String) {
//            super.onProgressUpdate(*values)
//            mLoginProcessStatus.setText(values[0])
//        }

        override fun doInBackground(vararg params: OUser): Void? {
            mUser = params[0]
            val daos = LoadingUtils.ArtifactsLoader(App.getContext(), mUser)
            daos.init()
            val productDao = App.getDao<ProductDao>(ProductDao::class.java)
            productDao.selectAll(QueryFields.all())
            return null
        }

        override fun onPostExecute(void: Void?) {
            startOdooActivity()

        }
    }


}
