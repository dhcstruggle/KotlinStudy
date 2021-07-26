package com.android.kotlinstudy.utils

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Toast

fun Handler.sendHandlerMsg(key: String, value: String, what: Int) {
    Bundle().let {
        it.putString(key, value)
        Message().apply {
            this.what = what
            data = it
            sendMessage(this)
        }
    }
}

object MiscUtils {
    val mTab: String by lazy { javaClass.simpleName }
    var mToast: Toast? = null
    fun showToastMsg(ctx: Context, msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(msg)
        }

        mToast!!.show()
    }

    fun showToastMsg(msg: String) {
        mToast?.apply {
            setText(msg)
            show()
        }
    }
}