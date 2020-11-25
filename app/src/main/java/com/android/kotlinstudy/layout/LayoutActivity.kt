package com.android.kotlinstudy.layout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.kotlinstudy.R

class LayoutActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_layout)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName
    }

    fun onLayoutClick(v: View?) {
        val cls: Class<*>? = when (v?.id) {
            R.id.btn_linear -> null
            R.id.btn_absolute -> null
            R.id.btn_frame -> null
            R.id.btn_table -> null
            R.id.btn_constrain -> null
            R.id.btn_relative -> null
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