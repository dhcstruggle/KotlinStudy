package com.android.kotlinstudy

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_text_view.*

//知识点
//1.显示字符串放在资源文件string.xml中
//2.显示字符串较长无法显示全部
//3.显示字符 + 图片
//4.增加中划线，并去锯齿
//5.增加下划线，有锯齿
//6.使用另一种方式加下划线，无锯齿
//7.跑马灯效果(未实现，需要解决)
//- 必须知道确切的宽度
//- 父布局需要知道这个子布局
//- 需要异步调用setSelected(true)接口让其选中

class TextViewActivity : AppCompatActivity() {
    private val mTab:String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_view)
        Log.d(mTab, "onCreate")
        title = this.javaClass.simpleName

        tv_4.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG//中划线
        tv_4.paint.isAntiAlias = true //去锯齿

        tv_5.paint.flags = Paint.UNDERLINE_TEXT_FLAG //下划线

        val strHtml = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<u> Chaos TextView <u>", Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml("<u> Chaos TextView <u>")
        }

        tv_6.text = strHtml
        tv_7.isSelected = true

    }
}