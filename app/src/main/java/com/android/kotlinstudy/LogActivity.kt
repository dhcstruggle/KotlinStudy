package com.android.kotlinstudy

import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_log.*
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

//知识点
//1.kotlin的类嵌套处理 ，内部类加上inner关键字，可以获取外部类的成员属性和方法
//2.不加inner关键字，则为嵌套类，嵌套类无法使用外部类的成员属性和方法

class LogActivity : AppCompatActivity() {
    private var mLogStat: Boolean = true
    val mLogHandler: Handler = LogHandler(this)
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)
        Log.d(mTab, "onCreate")

        title = this.javaClass.simpleName
        btn_change.isAllCaps = false
        btn_change.text = "Stop"
        btn_clear.isAllCaps = false
        btn_clear.text = "Clear"

        textLog.movementMethod = ScrollingMovementMethod.getInstance()

        LogThread().start()
    }

    fun onLogClick(v: View?) {
        when (v?.id) {
            R.id.btn_change -> {
                if (mLogStat)
                    btn_change.text = "Start"
                else
                    btn_change.text = "Stop"

                mLogStat = !mLogStat
            }

            R.id.btn_clear -> {
                textLog.text = ""
            }
            else -> Unit
        }
    }

    private inner class LogThread : Thread() {
        override fun run() {
            val logCmd: String = "logcat | grep " + android.os.Process.myPid()
            val timeFormat = SimpleDateFormat("yyyy-MM-ddHHmmss", Locale.CHINA)
            val date: String = timeFormat.format(Date(System.currentTimeMillis()))
            val path =
                Environment.getExternalStorageDirectory().absolutePath + "/log" + "/" + date + "-MF.log"

            val randomFile = RandomAccessFile(path, "rw")
            val logProcess: Process = Runtime.getRuntime().exec("sh")
            DataOutputStream(logProcess.outputStream).apply {
                write(logCmd.toByteArray())
                writeBytes("\n")
                flush()
                close()
            }

            val logReader = BufferedReader(InputStreamReader(logProcess.inputStream), 1024)
            while (true) {
                val line: String = logReader.readLine()

                if (line.isEmpty()) {
                    sleep(500)
                    continue
                }

                val msg = Message.obtain()
                msg.obj = line.trimIndent()
                val fileLength = randomFile.length()
                randomFile.seek(fileLength)
                randomFile.writeBytes(msg.obj.toString())
                if (mLogStat){
                    mLogHandler.sendMessage(msg)
                }
            }
        }
    }

    private inner class LogHandler(logActivity: LogActivity) : Handler() {
        private val mWeakReference: WeakReference<LogActivity> =
            WeakReference<LogActivity>(logActivity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val thisActivity: LogActivity? = mWeakReference.get()
            val tvLog: TextView? = thisActivity?.textLog
            tvLog?.let {
                it.append(msg.obj.toString())
                //滚动到最后一行
                val offset =
                    it.layout.getLineTop(it.lineCount) - it.height
                it.scrollTo(0, offset.coerceAtLeast(0))
            }
        }
    }
}