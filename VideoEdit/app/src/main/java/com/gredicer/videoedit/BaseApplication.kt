package com.gredicer.videoedit

import android.app.Application
import android.widget.TextView
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 Toast 框架
        ToastUtils.init(this);
    }
}