package com.android.kotlinstudy

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(this.mainLooper).postDelayed({
            Intent(this@SplashActivity, MainActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        }, 3000)
    }
}