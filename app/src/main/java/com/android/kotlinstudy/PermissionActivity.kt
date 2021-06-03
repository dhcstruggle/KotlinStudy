package com.android.kotlinstudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.kotlinstudy.utils.PermissionAccessUtil

class PermissionActivity : AppCompatActivity() {
    private val mTab by lazy { this.javaClass.name }
    private val mPermissionAccesser by lazy { PermissionAccessUtil(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionAccesser.RequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun onPermAccessClick(v: View?) {
        when (v?.id) {
            R.id.btn_permission_access -> {
                mPermissionAccesser.checkAndGetPermission()
            }
            else -> {

            }
        }
    }
}