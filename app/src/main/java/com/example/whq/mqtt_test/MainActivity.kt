package com.example.whq.mqtt_test

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken


/**
 * @author Vi
 * @date 2019/1/23 11:07 AM
 * @e-mail cfop_f2l@163.com
 */
class MainActivity : AppCompatActivity() {
    var mSharePreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mSharePreferences = getSharedPreferences("vii", MODE_PRIVATE)
        initView()
        mBtnAction.setOnClickListener { connectMQTT() }
    }

    fun initView() {
        mTvIP.setText(mSharePreferences?.getString("ip", ""))
        mTvName.setText(mSharePreferences?.getString("name", ""))
        mTvPwd.setText(mSharePreferences?.getString("pwd", ""))
        mTvTopic.setText(mSharePreferences?.getString("topic", ""))
        mTvClient.setText(mSharePreferences?.getString("client", ""))
    }

    /**
     * 链接MQTT
     */
    private fun connectMQTT() {
        val ipStr = mTvIP.text.toString()
        val nameStr = mTvName.text.toString()
        val pwdStr = mTvPwd.text.toString()
        val topicStr = mTvTopic.text.toString()
        val clientStr = mTvClient.text.toString()

        // Reset errors.
        mTvIP.error = null

        if (ipStr.isEmpty() || nameStr.isEmpty() || pwdStr.isEmpty() || topicStr.isEmpty() || clientStr.isEmpty()) {
            Toast.makeText(this, "请输入完整信息", Toast.LENGTH_SHORT).show()
            return
        }

        if (!ipStr.contains(".") && !ipStr.contains(":")) {
            mTvIP.error = "请输入正确的IP和端口号"
            return
        }

        // 保存记录
        val editor = mSharePreferences?.edit()
        editor?.putString("ip", ipStr)
        editor?.putString("name", nameStr)
        editor?.putString("pwd", pwdStr)
        editor?.putString("topic", topicStr)
        editor?.putString("client", clientStr)
        editor?.apply()

        // 开始链接
        val bean = ParamBean(ipStr, nameStr, pwdStr, topicStr, clientStr)
        MqttHelper.get().connect(this, bean, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                println("连接成功")
                //连接成功
                startActivity(Intent(this@MainActivity, DataActivity::class.java))
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                println("连接失败")
                //连接失败
                Toast.makeText(this@MainActivity, exception?.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}
