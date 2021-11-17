package com.android.kotlinstudy

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.kotlinstudy.utils.LogUtil
import com.android.kotlinstudy.utils.PermissionAccessUtil


class PermissionActivity : AppCompatActivity() {
    private val mTab by lazy { this.javaClass.name }
    private val mNeedPerms = arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val mPermAccessor by lazy { PermissionAccessUtil(this, mNeedPerms) }
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
                if (mPermAccessor.isAllPermAccessed()) {
                    LogUtil.d(mTab, "already get all perm")
                    Toast.makeText(this, "already get all perm", Toast.LENGTH_LONG).show()
                } else {
                    mPermAccessor.run()
                }
            }
            else -> {

            }
        }
    }
}