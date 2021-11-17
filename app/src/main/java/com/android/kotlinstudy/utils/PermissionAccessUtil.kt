package com.android.kotlinstudy.utils

import android.Manifest.permission
import android.app.Activity
import android.app.AppOpsManager
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

class PermissionAccessUtil (private val context: Context, perms : ArrayList<String>) {
    private val mTab by lazy { this.javaClass.name }
    private val mActivity by lazy { context as Activity }

    val PERM_CODE_START = 1000
    private val USAGE_PERM_CODE = 1001
    private val LOCAT_PERM_CODE = 1002
    private val EXSTORAGE_PERM_CODE = 1003

    data class PermissionInfo(val perm:String, var isGained:Boolean, val permCheck:()-> Boolean, val permGet:()->Unit)
    private val mPermissionArray by lazy {ArrayList<PermissionInfo>()}

    private val usagePermGet = {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                mActivity.startActivityForResult(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), USAGE_PERM_CODE)
        }
    }

    private val usagePermCheck:() -> Boolean = {
         if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            (AppOpsManager.MODE_ALLOWED ==
                    appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                            android.os.Process.myUid(), context.packageName))
            } else {
                Log.d(mTab, "SDK:${Build.VERSION.SDK_INT},do not have USAGE_STATS perm,but set true")
                true
            }
    }

    private val locationPermGet = {
        ArrayList<String>().run {
            add(permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(mActivity, this.toTypedArray(), LOCAT_PERM_CODE)
        }
    }

    private val locationPermCheck:() -> Boolean = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ContextCompat.checkSelfPermission(context, permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
        } else {
            Log.d(mTab, "SDK:${Build.VERSION.SDK_INT},do not have ${permission.ACCESS_FINE_LOCATION} perm,but set true")
            true
        }
    }

    private val exStoragePermGet = {
        when  {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).run {
                    data = Uri.parse("package:" + context.packageName)
                    mActivity.startActivityForResult(this, EXSTORAGE_PERM_CODE)
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                ArrayList<String>().run {
                    add(permission.WRITE_EXTERNAL_STORAGE)
                    ActivityCompat.requestPermissions(mActivity, this.toTypedArray(), EXSTORAGE_PERM_CODE)
                }
            }

            else -> { }
        }
    }

    private val exStoragePermCheck:() -> Boolean = {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Environment.isExternalStorageManager()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                (ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
            }
            else -> {
                Log.d(mTab, "SDK:${Build.VERSION.SDK_INT},do not have ${permission.WRITE_EXTERNAL_STORAGE} runtime perm,but set true")
                true
            }
        }
    }

    init {
        val permissionArray by lazy {ArrayList<PermissionInfo>()}
        permissionArray.add(PermissionInfo("USAGE_STATS", false, usagePermCheck, usagePermGet))
        permissionArray.add(PermissionInfo(permission.ACCESS_FINE_LOCATION, false, locationPermCheck, locationPermGet))
        permissionArray.add(PermissionInfo(permission.WRITE_EXTERNAL_STORAGE, false, exStoragePermCheck, exStoragePermGet))

        for (perm in perms) {
            for (permInfo in permissionArray) {
                if (perm == permInfo.perm) {
                    mPermissionArray.add(permInfo)
                }
            }
        }

        for (permInfo in mPermissionArray) {
            if (permInfo.permCheck()) {
                Log.d(mTab, "${permInfo.perm} already get permission")
                permInfo.isGained = true
            }
        }
    }

    fun run () {
        for (permInfo in mPermissionArray) {
            if (permInfo.permCheck()) {
                permInfo.isGained = true
            } else {
                Log.d(mTab, "start get permission:${permInfo.perm}")
                permInfo.permGet()
                return
            }
        }

        Log.d(mTab, "already get all permission")
    }

    fun isAllPermAccessed() :Boolean {
        for (permInfo in mPermissionArray) {
            Log.d(mTab, "${permInfo.perm}:${permInfo.isGained}")
            if (permInfo.isGained) {
                continue
            }

            return false
        }
        return true
    }

    fun permsResultCheck() {
        for (permission in mPermissionArray) {
            if (permission.permCheck()) {
                Log.d(mTab, "P:Get Permission(${permission.perm}) success")
                permission.isGained = true
            }
        }
    }
}