package com.android.kotlinstudy.utils

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionAccessUtil (private val context: Context) {
    private val mTab by lazy { this.javaClass.name }
    private val mActivity by lazy { context as Activity }
    private val mNeededPermission = ArrayList<String>()
    private val mPermissionArray by lazy {
        arrayListOf(
            // Wifi 扫描以及连接需要用到的权限
            permission.CHANGE_WIFI_STATE,
            permission.ACCESS_WIFI_STATE,
            permission.ACCESS_FINE_LOCATION,

            // 镜像制作时文件存储和扫描需要用到的权限
            permission.WRITE_EXTERNAL_STORAGE,
            permission.READ_EXTERNAL_STORAGE
        )
    }

    public fun checkAndGetPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:" + context.packageName)
                context.startActivity(intent)
            }
        }
        // android 23 版本之后有细分运行时权限，需要在这里申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (Permission in mPermissionArray) {
                if (ContextCompat.checkSelfPermission(context, Permission)
                    == PackageManager.PERMISSION_DENIED) {
                    mNeededPermission.add(Permission)
                }
            }

            if (mNeededPermission.isNotEmpty()) {
                Log.d(mTab, "need to get permission:$mNeededPermission")
                ActivityCompat.requestPermissions(mActivity, mNeededPermission.toTypedArray(), 1)
            } else {
                Log.d(mTab, "already get all permission:$mPermissionArray")
                ToastUtil.showMsg(context, "already get all permission")
            }

        } else {
            Log.d(mTab, "SDK=${Build.VERSION.SDK_INT}, no runTime Permission")
        }
    }

    fun RequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            1 -> {
                for (i in grantResults.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(mTab, "Get Permission(${permissions[i]}) success")
                    } else {
                        Log.d(mTab, "Get Permission(${permissions[i]}) Fail")
                    }
                }
            }
        }
    }

}