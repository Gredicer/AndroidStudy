package com.gredicer.basestudy

import android.app.Application
import android.content.Context
import com.hjq.toast.ToastUtils

/**
 * author : Gredicer
 * time   : 2022/1/29
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MainApplication.applicationContext = this.applicationContext

        // 初始化 Toast 框架
        ToastUtils.init(this);
    }



    companion object {
        var applicationContext: Context? = null
    }
}