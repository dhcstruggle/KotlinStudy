package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_button.*
//知识点
//1.btn_1 设置文字颜色白色，背景红色
//2.btn_2 设置背景为圆角样式
//3.btn_3 设置点击事件，设置背景为圆圈样式
//4.btn_4 设置点击事件，设置点击时背景颜色变化
//5.btn_tv1 设置点击事件，设置背景


class ButtonActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_button)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName

        btn_3.setOnClickListener {
            Toast.makeText(this, "btn3 Clicked", Toast.LENGTH_SHORT).show()
        }

        btn_tv1.setOnClickListener {
            Toast.makeText(this, "btn_tv1 Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun btn4OnClick(v: View){
        Toast.makeText(this, "btn4 Clicked", Toast.LENGTH_SHORT).show()
    }
}