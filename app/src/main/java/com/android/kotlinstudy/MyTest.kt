package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MyTest : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_test)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName
    }
}