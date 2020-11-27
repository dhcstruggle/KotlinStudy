package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_handler.*

//知识点
//1.理解handler looper概念
//2.解决warning问题，传入当前activity 进程 looper
//3.发送信息，接受信息和处理信息
//4.发送消息各种形式：runable，


class HandlerActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }
    private val mHandler by lazy { MyHandler() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handler)

        btn_msg1.setOnClickListener {
            mHandler.sendEmptyMessage(1)
        }

        btn_msg2.setOnClickListener {
            mHandler.sendEmptyMessage(2)
        }

        mHandler.post {
            Toast.makeText(this@HandlerActivity, "get handler post", Toast.LENGTH_LONG).show()
            Log.d(mTab, "get handler post")
        }
    }

    inner class MyHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    Toast.makeText(this@HandlerActivity, "get handler what 1", Toast.LENGTH_LONG).show()
                    Log.d(mTab, "get handler what 1")
                }
                else -> {
                    Toast.makeText(this@HandlerActivity, "get handler else", Toast.LENGTH_LONG).show()
                    Log.d(mTab, "get handler else")
                }
            }
        }
    }
}