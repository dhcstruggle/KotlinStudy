package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_radio_btn.*

//知识点
//选择器的使用，主要是看布局文件

class RadioBtnActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio_btn)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName

        rb1.setOnClickListener {
            if (rb1.isChecked)
                Toast.makeText(this, "Boy selected", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this, "Girl selected", Toast.LENGTH_SHORT).show()

        }
    }
}