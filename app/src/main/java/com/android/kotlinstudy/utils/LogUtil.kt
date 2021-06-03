package com.android.kotlinstudy.utils

import android.os.Environment
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object LogUtil {
    private val mDateFormat: String =
        SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(Date())
    private val mLogDirPath = Environment.getExternalStorageDirectory().absolutePath + "/log"
    private val mLogPath = "$mLogDirPath/$mDateFormat-VF.log"
    private var mLogFile: RandomAccessFile? = null

    init {
        File(mLogDirPath).apply {
            if (!exists()) {
                mkdir()
            }
        }

        try {
            mLogFile = RandomAccessFile(mLogPath, "rw")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    fun d(tab:String, log:String) {
        Log.d(tab, log)
        p("$tab-d",log)
    }

    fun i(tab:String, log:String) {
        Log.i(tab, log)
        p("$tab-i",log)
    }

    fun e(tab:String, log:String) {
        Log.e(tab, log)
        p("$tab-e",log)
    }

    fun p(tab:String, log:String) {
        if (log.isEmpty()) {
            return
        }

        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA).format(Date())
        mLogFile?.let {
            synchronized(it) {
                val line = "$dateFormat:($tab)$log"
                it.writeBytes("$line\n")
            }
        }
    }
}
