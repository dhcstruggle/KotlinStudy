package com.android.kotlinstudy

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.kotlinstudy.utils.LogUtil

//知识点
//1.kotlin的类嵌套处理 ，内部类加上inner关键字，可以获取外部类的成员属性和方法
//2.不加inner关键字，则为嵌套类，嵌套类无法使用外部类的成员属性和方法

class LogActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        Log.d(mTab, "onCreate")
    }

    fun onLogClick(v: View?) {
        when (v?.id) {
            R.id.btn_log -> {
                LogUtil.d(mTab, "LogLog:$mTab")
            }
            else -> Unit
        }
    }
}