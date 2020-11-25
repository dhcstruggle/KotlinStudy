package com.android.kotlinstudy

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_system_info.*

//知识点
//系统信息获取接口


class SystemInfo : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_info)
        Log.d(mTab, "onCreate")
        title = "系统信息"

        val sysInfo = """
            PRODUCT: ${Build.PRODUCT}
            CPU_ABI: ${Build.CPU_ABI}
            MODEL: ${Build.MODEL}
            SDK: ${Build.VERSION.SDK}
            VERSION.RELEASE: ${Build.VERSION.RELEASE}
            DEVICE: ${Build.DEVICE}
            BRAND: ${Build.BRAND}
            BOARD: ${Build.BOARD}
            ID: ${Build.ID}
            MANUFACTURER: ${Build.MANUFACTURER}
            USER: ${Build.USER}
            """.trimIndent()

        tv_sys_info.text = sysInfo
    }
}