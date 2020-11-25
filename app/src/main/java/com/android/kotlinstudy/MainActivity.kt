package com.android.kotlinstudy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.kotlinstudy.layout.LayoutActivity


class MainActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(mTab, "onCreate")
    }

    fun onMainClick(v: View?) {
        val cls: Class<*>? = when (v?.id) {
            R.id.btn_mytest -> MyTest::class.java
            R.id.btn_layout -> LayoutActivity::class.java
            R.id.btn_log -> LogActivity::class.java
            R.id.btn_bank_service -> null
            R.id.btn_service -> null
            R.id.btn_broad -> null
            R.id.btn_data -> null
            R.id.btn_handler -> null
            R.id.btn_textview -> TextViewActivity::class.java
            R.id.btn_button -> ButtonActivity::class.java
            R.id.btn_edittext -> EditTextActivity::class.java
            R.id.btn_radiobtn -> RadioBtnActivity::class.java
            R.id.btn_checkbox -> CheckBoxActivity::class.java
            R.id.btn_imageview -> null
            R.id.btn_listview -> null
            R.id.btn_gridview -> null
            R.id.btn_recyclerview -> null
            R.id.btn_webview -> null
            R.id.btn_toast -> null
            R.id.btn_dialog -> null
            R.id.btn_progress -> null
            R.id.btn_cus_dialog -> null
            R.id.btn_popup_window -> null
            R.id.btn_life_cycle -> null
            R.id.btn_jump -> null
            R.id.btn_fragment -> null
            R.id.btn_event -> null
            R.id.btn_sys_infos -> SystemInfo::class.java
            else -> null
        }

        cls?.let {
            val intent = Intent(this, it)
            startActivity(intent)
            return
        }

        Toast.makeText(this, "Can not get class" + v?.id, Toast.LENGTH_SHORT).show()
    }
}