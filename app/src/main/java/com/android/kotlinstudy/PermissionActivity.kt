package com.android.kotlinstudy

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.kotlinstudy.utils.LogUtil
import com.android.kotlinstudy.utils.PermissionAccessUtil


class PermissionActivity : AppCompatActivity() {
    private val mTab by lazy { this.javaClass.name }
    private val mPermAccessor by lazy { PermissionAccessUtil(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode > mPermAccessor.PERM_CODE_START) {
            mPermAccessor.permsResultCheck()
            if (mPermAccessor.isAllPermAccessed()) {
                LogUtil.d(mTab, "already get all perm")
            } else {
                mPermAccessor.run()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode > mPermAccessor.PERM_CODE_START) {
            mPermAccessor.permsResultCheck()
            if (mPermAccessor.isAllPermAccessed()) {
                LogUtil.d(mTab, "already get all perm")
            } else {
                mPermAccessor.run()
            }
        }
    }

    fun onPermAccessClick(v: View?) {
        when (v?.id) {
            R.id.btn_permission_access -> {
                mPermAccessor.run()
            }
            else -> {

            }
        }
    }
}