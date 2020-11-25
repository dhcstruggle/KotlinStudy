package com.android.kotlinstudy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_edit_text.*

//知识点
//1.编辑框变化时的动作
//2.登录按钮设置动作的另一种方式，匿名函数


class EditTextActivity : AppCompatActivity() {
    private val mTab:String by lazy { this.javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_text)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName

        et_1.doOnTextChanged {text, start, count, after ->
            Log.d("onTextChanged: ", text.toString())
            Toast.makeText(this, text.toString(), Toast.LENGTH_SHORT).show()
        }

        et_btn1.setOnClickListener {
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
        }
    }
}