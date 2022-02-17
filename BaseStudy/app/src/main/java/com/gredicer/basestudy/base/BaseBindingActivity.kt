package com.gredicer.basestudy.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.LogUtils
import com.dylanc.viewbinding.base.ViewBindingUtil
import com.gredicer.basestudy.event.BindEventBus
import com.gredicer.basestudy.event.EventBusUtils
import com.gredicer.basestudy.util.DarkModeUtils


/**
 * author : Gredicer
 * time   : 2022/1/26
 */
abstract class BaseBindingActivity<VB : ViewBinding> : AppCompatActivity() {

    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ViewBindingUtil.inflateWithGeneric(this, layoutInflater)
        setContentView(binding.root)


        // 若使用BindEventBus注解，则绑定EventBus
        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBusUtils.register(this)
        }

        BarUtils.setStatusBarLightMode(this, !DarkModeUtils.isDarkMode(this))
        BarUtils.transparentStatusBar(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 若使用BindEventBus注解，则解绑定EventBus
        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBusUtils.unregister(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val darkModeStatus = if (DarkModeUtils.isDarkMode(this)) "夜间模式已打开" else "夜间模式已关闭"
        LogUtils.d(darkModeStatus)
    }
}