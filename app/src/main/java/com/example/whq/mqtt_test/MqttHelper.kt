package com.example.whq.mqtt_test

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

/**
 * MQTT帮助类
 *
 * @author Vi
 * @date 2019/1/23 3:00 PM
 * @e-mail cfop_f2l@163.com
 */
class MqttHelper private constructor() {
    private lateinit var mqttAndroidClient: MqttAndroidClient
    private lateinit var options: MqttConnectOptions

    private var mMqttCallBack: ViMqttCallBack? = null
    private var SERVICETOPIC: String = ""

    companion object {
        private var instance: MqttHelper? = null
            get() {
                if (field == null) {
                    field = MqttHelper()
                }
                return field
            }

        @Synchronized
        fun get(): MqttHelper {
            return instance!!
        }
    }

    fun connect(context: Context, bean: ParamBean, mqttCallBack: ViMqttCallBack) {
        this.mMqttCallBack = mqttCallBack
        SERVICETOPIC = bean.topic
        mqttAndroidClient = MqttAndroidClient(context, bean.ip, bean.client, MemoryPersistence())

        options = MqttConnectOptions()
        // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.isCleanSession = false
        // 设置连接的用户名
        options.userName = bean.name
        // 设置连接的密码
        options.password = bean.pwd.toCharArray()
        // 设置超时时间 单位为秒
        options.connectionTimeout = 10
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.keepAliveInterval = 20
        // 关闭自动链接一切变成手动
        //options.setAutomaticReconnect(true);
        // 设置MQTT监听并且接受消息
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {
                //mMqttCallBack?.connectionLost(cause)
                mMqttCallBack?.showLog("【MQTT】失去连接$cause")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                val data = String(message.payload)
                //mMqttCallBack?.messageArrived(topic, message)
                mMqttCallBack?.showLog("【MQTT】接收->$data")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {
                //mMqttCallBack?.deliveryComplete(token)
                mMqttCallBack?.showLog("【MQTT】发送成功")
            }
        })

        // 开始链接
        mqttAndroidClient.connect(options, this, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                mMqttCallBack?.onSuccess()
                mMqttCallBack?.showLog("【MQTT】连接成功")

                // 订阅client话题
                mqttAndroidClient.subscribe(bean.client, 1)
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                mMqttCallBack?.onFailure(exception)
                mMqttCallBack?.showLog("【MQTT】连接失败 error:$exception")
            }
        })
    }

    /**
     * 断开服务器链接
     */
    fun disConnect() {
        mMqttCallBack?.showLog("【MQTT】断开链接")

        mqttAndroidClient.disconnect()
    }

    /**
     * 发布消息
     *
     * @param msg 消息内容
     */
    fun publish(msg: String) {
        mMqttCallBack?.showLog("【MQTT】发送>>>>>>$msg")

        val mqttMessage = MqttMessage()
        mqttMessage.payload = msg.toByteArray()
        mqttMessage.isRetained = true
        //0:最多一次的传输；1：至少一次的传输；2： 只有一次的传输
        mqttMessage.qos = 0
        mqttAndroidClient.publish(SERVICETOPIC, mqttMessage)
    }
}

interface ViMqttCallBack {

    // MQTT连接成功
    fun onSuccess()

    // MQTT连接失败
    fun onFailure(exception: Throwable)

//    // MQTT失去连接
//    fun connectionLost(cause: Throwable)
//
//    // 收到消息
//    fun messageArrived(topic: String, message: MqttMessage)
//
//    // 消息发送成功
//    fun deliveryComplete(token: IMqttDeliveryToken)

    // log信息
    fun showLog(logStr: String)

}