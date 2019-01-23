package com.example.whq.mqtt_test

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_data.*

/**
 * MQTT数据测试类
 *
 * @author Vi
 * @date 2019/1/23 4:27 PM
 * @e-mail cfop_f2l@163.com
 */
class DataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        initViews()
    }

    private fun initViews() {
        initToolbar()
        mBtnChoose.setOnClickListener {

        }

        mBtnAdd.setOnClickListener {

        }

        mBtnSend.setOnClickListener {

        }
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

            AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否关闭页面并断开MQTT链接?")
                .setPositiveButton("确定") { _, _ -> finish() }
                .setNegativeButton("取消") { _, _ -> }
                .show()
        }
    }

}