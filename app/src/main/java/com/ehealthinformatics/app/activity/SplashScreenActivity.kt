package com.ehealthinformatics.app.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler

import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    private val splashTime = 3000L
    private lateinit var myHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myHandler = Handler()
        myHandler.postDelayed({
            startOdooActivity()
        }, splashTime)
    }

    private fun startOdooActivity() {
        startActivity(Intent(this, OdooActivity::class.java))
        finish()
    }

}
