package com.gredicer.basestudy.event

import org.greenrobot.eventbus.EventBus


object EventBusUtils {
    /**
     * 绑定 接受者
     * @param subscriber
     */
    fun register(subscriber: Any?) {
        EventBus.getDefault().register(subscriber)
    }

    /**
     * 解绑定
     * @param subscriber
     */
    fun unregister(subscriber: Any?) {
        EventBus.getDefault().unregister(subscriber)
    }

    /**
     * 发送消息(事件)
     * @param event
     */
    fun sendEvent(event: Event<*>?) {
        EventBus.getDefault().post(event)
    }

    /**
     * 发送 粘性 事件
     *
     * 粘性事件，在注册之前便把事件发生出去，等到注册之后便会收到最近发送的粘性事件（必须匹配）
     * 注意：只会接收到最近发送的一次粘性事件，之前的会接受不到。
     * @param event
     */
    fun sendStickyEvent(event: Event<*>?) {
        EventBus.getDefault().postSticky(event)
    }
}