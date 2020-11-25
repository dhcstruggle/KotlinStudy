package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

//知识点
//1. 基础样式
//2. 设置选择框样式，选择器，以及选择控件的背景变化

class CheckBoxActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_box)
        Log.d(mTab, "onCreate")
    }
}