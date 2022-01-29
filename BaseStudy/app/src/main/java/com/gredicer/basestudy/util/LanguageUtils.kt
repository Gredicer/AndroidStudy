package com.gredicer.basestudy.util

import android.content.Context
import android.content.res.Configuration
import java.util.*


object LanguageUtils {

    /** 获取当前语言*/
    fun getLanguage(context: Context): Locale = context.resources.configuration.locale

    /** 设置语言*/
    fun setLanguage(context: Context, locale: Locale) {
        //获取res对象
        val resources = context.resources
        //获得设置对象
        val config: Configuration = resources.configuration
        //获取屏幕参数 主要是分辨率,像素等
        val dm = resources.displayMetrics
        //语言
        config.locale = locale
        resources.updateConfiguration(config, dm)
    }
}