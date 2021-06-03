package com.android.kotlinstudy.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ProgressDialog
import android.content.*
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.PatternMatcher
import android.provider.Settings
import android.util.Log


class WifiAutoConnectUtil(private val context: Context, private val prgDialog: ProgressDialog) : Thread() {
    private val mTab by lazy { this.javaClass.name }
    private val mPrgDialog by lazy { prgDialog }
    private val mActivity by lazy { context as Activity }
    private val mAm by lazy { context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
    private val mWifiManager by lazy { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
    private val mWifiScanReceiver by lazy { WifiScanReceiver() }
    private val mSSIDMagic by lazy { "MeiYa@@" }
    var mWifiConnectDone = false
    var mVFSSID:String ? = null

    init {
    }

    override fun run() {
        mWifiConnectDone = false
        mVFSSID = null

        //打开wifi开关
        if (mWifiManager.isWifiEnabled) {
            mActivity.runOnUiThread{ ToastUtil.showMsg(context, "Wifi already on") }
        } else {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                mWifiManager.setWifiEnabled(true)
            } else {
                //网络开关接口在 28 版本之后，就无法通过代码进行开关，这里调用原生的设置接口让用户手动打开
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                sleep(500)
                //这里判断用户是否打开，没有打开的话继续调用接口
                while (true) {
                    val cn:ComponentName ? = mAm.getRunningTasks(1)[0].topActivity
                    if (cn != null) {
                        if (cn.className == context.javaClass.name) {
                            if (mWifiManager.isWifiEnabled)  {
                                break
                            } else {
                                mActivity.runOnUiThread { ToastUtil.showMsg(context, "检测到未打开wifi开关，请打开wifi开关") }
                                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                                sleep(1000)
                            }
                        }
                    }
                    sleep(500)
                }
            }
        }

        val connectionInfo = mWifiManager.connectionInfo
        if (connectionInfo.ssid.contains(mSSIDMagic)) {
            mActivity.runOnUiThread {
                ToastUtil.showMsg(context, "热点${connectionInfo.ssid}已连接")
            }
            return
        }

        //扫描wifi热点
        mActivity.runOnUiThread {
            mPrgDialog.run {
                setTitle("提示")
                setMessage("正在扫描汽车取证大师Wifi热点")
                setCancelable(false)
                show()
            }
        }

        context.registerReceiver(mWifiScanReceiver, IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION))
        mWifiManager.startScan()

        //等待自动连接成功
        val timeOut = 30
        var timeOutCount = 0
        while(timeOutCount < timeOut) {
            val connectionInfo = mWifiManager.connectionInfo
            if (mVFSSID.equals(connectionInfo.ssid.trim('"'))) {
                mWifiConnectDone = true
                mActivity.runOnUiThread {
                    mPrgDialog.dismiss()
                    ToastUtil.showMsg(context, "热点连接成功")
                }
                return
            }

            sleep(1000)
            timeOutCount ++
        }

        //提示
        mActivity.runOnUiThread {
            mPrgDialog.dismiss()
            ToastUtil.showMsg(context, "无法找到热点")
        }
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

    private fun wifiConnect(SSID: String, password: String){
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

            val connectivityManager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager.requestNetwork(request, object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    mWifiConnectDone = true
                }

                override fun onUnavailable() {
                    mPrgDialog.dismiss()
                    ToastUtil.showMsg(context, "热点连接失败")
                }
            })
        } else {
            mWifiManager.enableNetwork(mWifiManager.addNetwork(configWifiInfo(SSID, password, 2)), true)
        }
    }

    inner class WifiScanReceiver: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            intent?.run {
                when (action) {
                    SCAN_RESULTS_AVAILABLE_ACTION -> {
                        val scanResults = mWifiManager.scanResults
                        val strSSIDList = ArrayList<String>()
                        for (scanResult in scanResults) {
                            if (mSSIDMagic in scanResult.SSID) {
                                context.unregisterReceiver(mWifiScanReceiver)
                                mPrgDialog.setMessage("找到汽车取证大师WIFI热点(${scanResult.SSID})，正在尝试连接...")
                                mVFSSID = scanResult.SSID
                                wifiConnect(scanResult.SSID, "12345678")
                            }

                            strSSIDList.add(scanResult.SSID)
                        }
                        Log.e(mTab, "scanResult:$strSSIDList")
                    }
                    else -> {}
                }
            }
        }
    }
}