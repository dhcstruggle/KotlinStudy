package com.android.kotlinstudy.utils

import android.os.Bundle
import android.os.Handler
import android.os.Message

fun Handler.sendHandlerMsg(key:String, value:String, what:Int) {
    Bundle().let {
        it.putString(key,value)
        Message().apply {
            this.what = what
            data = it
            sendMessage(this)
        }
    }
}

object ExUtils {
    val mTab: String by lazy { javaClass.simpleName }
}