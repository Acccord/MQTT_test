package com.example.whq.mqtt_test

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.KeyEvent
import android.widget.ScrollView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_data.*

/**
 * MQTT数据测试类
 *
 * @author Vi
 * @date 2019/1/23 4:27 PM
 * @e-mail cfop_f2l@163.com
 */
class DataActivity : AppCompatActivity(), ViMqttCallBack {
    private var mSharePreferences: SharedPreferences? = null
    private var mTouchTime: Long = 0//滑动后5秒内不自动滚动

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        mSharePreferences = getSharedPreferences("vii", MODE_PRIVATE)

        initViews()

//        val dataStr = "{\"clientID\":\"352693080292986\",\"sn\":\"16177\",\"version\":\"10802\",\"mType\":\"ACA\"}"
//        mEtInput.setText(dataStr)

        mEtInput.setText(mSharePreferences?.getString("data", ""))
    }

    private fun initViews() {
        initToolbar()
        mBtnChoose.setOnClickListener {

        }

        mBtnAdd.setOnClickListener {

        }

        mBtnSend.setOnClickListener {
            val sendText = mEtInput.text.toString()
            if (sendText.isEmpty()) {
                Toast.makeText(this@DataActivity, "请输入发送内容", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 保存记录
            val editor = mSharePreferences?.edit()
            editor?.putString("data", sendText)
            editor?.apply()

            MqttHelper.get().publish(sendText)
        }

        mTvClear.setOnClickListener {
            mTvLog.text = ""
        }

        mScrollView.setOnTouchListener { _, _ ->
            mTouchTime = System.currentTimeMillis()
            false
        }
        MqttHelper.get().addCallBack(this)
    }

    private fun initToolbar() {
        setSupportActionBar(mToolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }
        mToolbar.title = "数据收发测试"
        mToolbar.setNavigationOnClickListener {
            onBack()
        }
    }

    /**
     * 展示log信息
     */
    override fun showLog(logStr: String) {
        if (mTvLog.text.length > 50000) {
            mTvLog.text = ""
        }

        mTvLog.append(Html.fromHtml("<br>$logStr"))

        //滑动后5秒内不自动滚动
        val t = System.currentTimeMillis()
        if (t - mTouchTime > 5 * 1000) {
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    /**
     * 屏蔽返回按键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack()
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        MqttHelper.get().disConnect()
    }

    fun onBack() {
        AlertDialog.Builder(this).setTitle("提示")
            .setMessage("是否关闭页面并断开MQTT链接?")
            .setPositiveButton("确定") { _, _ -> finish() }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }
}