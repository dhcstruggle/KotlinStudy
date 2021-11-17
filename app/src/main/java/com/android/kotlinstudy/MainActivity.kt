package com.android.kotlinstudy

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewConfiguration
import android.widget.Toast
import com.android.kotlinstudy.layout.LayoutActivity
import com.android.kotlinstudy.utils.LogUtil
import java.lang.Exception
import java.lang.reflect.Field


class MainActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)
        val info = packageManager.getPackageInfo(packageName, 0)
        title = title.toString() + "-" + info.versionName
        setOverFlowShowingAlways()
        Log.d(mTab, "onCreate")
    }

    fun onMainClick(v: View?) {
        val cls: Class<*>? = when (v?.id) {
            R.id.btn_hotplug -> HotPlugActivity::class.java
            R.id.btn_mytest -> ExStorageTestActivity::class.java
            R.id.btn_layout -> LayoutActivity::class.java
            R.id.btn_log -> LogActivity::class.java
            R.id.btn_bank_service -> null
            R.id.btn_service -> ServiceActivity::class.java
            R.id.btn_broad -> null
            R.id.btn_data -> null
            R.id.btn_handler -> HandlerActivity::class.java
            R.id.btn_textview -> TextViewActivity::class.java
            R.id.btn_button -> ButtonActivity::class.java
            R.id.btn_edittext -> EditTextActivity::class.java
            R.id.btn_radiobtn -> RadioBtnActivity::class.java
            R.id.btn_checkbox -> CheckBoxActivity::class.java
            R.id.btn_imageview -> null
            R.id.btn_listview -> null
            R.id.btn_gridview -> null
            R.id.btn_recyclerview -> null
            R.id.btn_webview -> WebViewActivity::class.java
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
            R.id.btn_wifi_infos -> WifiInfo::class.java
            R.id.btn_perm -> PermissionActivity::class.java
            R.id.btn_app_usage -> AppUsageActivity::class.java
            R.id.btn_alert -> AlertActivity::class.java
            else -> null
        }

        cls?.let {
            val intent = Intent(this, it)
            startActivity(intent)
            return
        }

        Toast.makeText(this, "Can not get class" + v?.id, Toast.LENGTH_SHORT).show()
    }

    private fun setOverFlowShowingAlways() {
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField: Field? = ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
            menuKeyField?.let {
                menuKeyField.isAccessible = true
                menuKeyField.setBoolean(config, false)
                return
            }

            LogUtil.d(mTab, "Field get Fail!")
        } catch (e: Exception) {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_info -> {
                val intent = Intent(Settings.ACTION_DEVICE_INFO_SETTINGS)
                startActivity(intent)
            }

            R.id.action_settings -> {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }

            R.id.action_dials -> {
                startActivity(Intent(Intent.ACTION_DIAL))
            }

            R.id.action_develop -> {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
            }

            R.id.reboot_recovery -> {
                val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
                powerManager.reboot("recovery")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val mHandler  = Handler(Looper.getMainLooper()) {msg ->
        when (msg.what) {

        }
    }
}