package com.android.kotlinstudy

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.os.PatternMatcher
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.kotlinstudy.utils.ToastUtil
import com.android.kotlinstudy.utils.WifiAutoConnectUtil
import kotlinx.android.synthetic.main.activity_wifi_info.*
import java.lang.Thread.sleep
import kotlin.concurrent.thread


class WifiInfo : AppCompatActivity() {
    private val mTab by lazy { this.javaClass.name }
    private val mPrgDialog by lazy { ProgressDialog(this) }
    private val mWifiManager by lazy { applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager}
    private val mWifiScanReceiver by lazy { WifiScanReceiver() }
    private val mNeededPermission = ArrayList<String>()
    var mVFSSID:String ? = null
    private val mPermissionArray by lazy {
        arrayListOf(
            // Wifi 扫描以及连接需要用到的权限
            CHANGE_WIFI_STATE,
            ACCESS_WIFI_STATE,
            ACCESS_FINE_LOCATION

            // 镜像制作时文件存储和扫描需要用到的权限

        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_info)
        checkAndGetPermission()
        ToastUtil.showMsg(this, "WifiInfo onCreate")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(
                            this,
                            "Get Permission(${permissions[i]}) success",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Get Permission(${permissions[i]}) Fail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkAndGetPermission() {
        // android 23 版本之后有细分运行时权限，需要在这里申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (Permission in mPermissionArray) {
                if (ContextCompat.checkSelfPermission(this, Permission)
                    == PackageManager.PERMISSION_DENIED) {
                    mNeededPermission.add(Permission)
                }
            }

            if (mNeededPermission.isNotEmpty()) {
                Log.d(mTab, "need to get permission:$mNeededPermission")
                ActivityCompat.requestPermissions(this, mNeededPermission.toTypedArray(), 1)
            } else {
                Log.d(mTab, "already get all permission:$mPermissionArray")
            }

        } else {
            Log.d(mTab, "SDK=${Build.VERSION.SDK_INT}, no runTime Permission")
        }
    }

    @SuppressLint("MissingPermission")
    private fun removeWifi(SSID: String) : Boolean {

        val cfgNetworks = mWifiManager.getConfiguredNetworks()
        for (cfgNetwork in cfgNetworks) {
            if (SSID == cfgNetwork.SSID) {
                mWifiManager.removeNetwork(cfgNetwork.networkId)
                return true
            }
        }

        return false
    }

    @SuppressLint("MissingPermission")
    fun configWifiInfo(SSID: String, password: String, type: Int): WifiConfiguration? {
        var config: WifiConfiguration? = null
        val existingConfigs = mWifiManager.configuredNetworks
        for (existingConfig in existingConfigs) {
                if (existingConfig == null) continue
                if (existingConfig.SSID == "\"" + SSID + "\"") {
                    config = existingConfig
                    break
                }
        }

        if (config == null) {
            config = WifiConfiguration()
        }

        config.allowedAuthAlgorithms.clear()
        config.allowedGroupCiphers.clear()
        config.allowedKeyManagement.clear()
        config.allowedPairwiseCiphers.clear()
        config.allowedProtocols.clear()
        config.SSID = "\"" + SSID + "\""

        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) { // WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true
            config.wepKeys[0] = "\"" + password + "\""
            config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.SHARED)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            config.allowedGroupCiphers
                .set(WifiConfiguration.GroupCipher.WEP104)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            config.wepTxKeyIndex = 0
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\""
            config.hiddenSSID = true
            config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP)
            config.status = WifiConfiguration.Status.ENABLED
        }
        return config
    }


    fun wifiConnect(SSID: String, password: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val specifier = WifiNetworkSpecifier.Builder()
                .setSsidPattern(PatternMatcher(SSID, PatternMatcher.PATTERN_PREFIX))
            .setWpa2Passphrase(password)
            .build()

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build()

            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager.requestNetwork(request, object : NetworkCallback() {
                override fun onAvailable(network: Network) {
                    mPrgDialog.dismiss()
                    Toast.makeText(this@WifiInfo, "热点连接成功", Toast.LENGTH_SHORT).show()
                }

                override fun onUnavailable() {
                    mPrgDialog.dismiss()
                    Toast.makeText(this@WifiInfo, "热点连接失败", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
//            var netId = -1;
//            /*先执行删除wifi操作，1.如果删除的成功说明这个wifi配置是由本APP配置出来的；
//                               2.这样可以避免密码错误之后，同名字的wifi配置存在，无法连接；
//                               3.wifi直接连接成功过，不删除也能用, netId = getExitsWifiConfig(SSID).networkId;*/
//            netId = if (removeWifi(SSID)) {
//                //移除成功，就新建一个
//                mWifiManager.addNetwork(configWifiInfo(SSID, password, 2));
//            } else {
//                //删除不成功，要么这个wifi配置以前就存在过，要么是还没连接过的
//                if (getExitsWifiConfig(SSID) != null) {
//                    //这个wifi是连接过的，如果这个wifi在连接之后改了密码，那就只能手动去删除了
//                    getExitsWifiConfig(SSID).networkId;
//                } else {
//                    //没连接过的，新建一个wifi配置
//                    mWifiManager.addNetwork(configWifiInfo(SSID, password, 2));
//                }
//            }
            //这个方法的第一个参数是需要连接wifi网络的networkId，第二个参数是指连接当前wifi网络是否需要断开其他网络
            //无论是否连接上，都返回true。。。。

            mWifiManager.enableNetwork(
                mWifiManager.addNetwork(configWifiInfo(SSID, password, 2)),
                true
            )
            thread {
                while (true) {
                    val connectionInfo = mWifiManager.connectionInfo
                    if (mVFSSID.equals(connectionInfo.ssid.trim('"'))) {
                        mPrgDialog.dismiss()
                        runOnUiThread {
                            Toast.makeText(this@WifiInfo, "热点连接成功", Toast.LENGTH_SHORT).show()
                        }
                        break
                    }

                    sleep(1000)
                }
            }
        }
    }


    inner class WifiScanReceiver:BroadcastReceiver () {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                when (action) {
                    SCAN_RESULTS_AVAILABLE_ACTION -> {
                        val scanResults = mWifiManager.scanResults
                        val strSSIDList = ArrayList<String>()
                        for (scanResult in scanResults) {
                            if ("MeiYa@@" in scanResult.SSID) {
                                unregisterReceiver(mWifiScanReceiver)
                                mPrgDialog.setMessage("找到汽车取证大师WIFI热点(${scanResult.SSID})，正在尝试连接...")
                                mVFSSID = scanResult.SSID
                                wifiConnect(scanResult.SSID, "12345678")
                            }

                            strSSIDList.add(scanResult.SSID)
                        }
                        tv_wifi_info.text = strSSIDList.toString()


                    }
                }
            }
        }
    }

    fun onWifiClick(v: View?) {
        when (v?.id) {
            R.id.btn_wifi_on -> {
                if (mWifiManager.isWifiEnabled) {
                    Toast.makeText(this, "Wifi already on", Toast.LENGTH_SHORT).show()
                } else {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        mWifiManager.setWifiEnabled(true)
                    } else {
                        //网络开关接口在 28 版本之后，就无法通过代码进行开关，这里调用原生的设置接口让用户手动打开
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                }
            }

            R.id.btn_wifi_off -> {
                if (mWifiManager.isWifiEnabled) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        mWifiManager.setWifiEnabled(false)
                    } else {
                        //网络开关接口在 28 版本之后，就无法通过代码进行开关，这里调用原生的设置接口让用户手动关闭
                        startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                    }
                } else {
                    Toast.makeText(this, "Wifi already off", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btn_wifi_scan -> {
                mPrgDialog.run {
                    setTitle("提示")
                    setMessage("正在扫描汽车取证大师Wifi热点")
                    setCancelable(false)
                    show()
                }
                registerReceiver(mWifiScanReceiver, IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION))
                mWifiManager.startScan()
            }

            R.id.btn_test_connect -> {
                val wifiAutoConnectThread by lazy { WifiAutoConnectUtil(this, mPrgDialog) }
                wifiAutoConnectThread.start()
            }
        }
    }
}