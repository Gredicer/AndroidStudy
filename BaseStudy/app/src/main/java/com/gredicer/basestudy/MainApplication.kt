package com.gredicer.basestudy

import android.app.Application
import android.content.Context

/**
 * author : Gredicer
 * time   : 2022/1/29
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MainApplication.applicationContext = this.applicationContext
    }

    companion object {
        var applicationContext: Context? = null
    }
}