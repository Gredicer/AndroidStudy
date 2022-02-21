package com.gredicer.camerastudy

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.gredicer.camerastudy.base.BaseBindingActivity
import com.gredicer.camerastudy.base.BasePermissionActivity
import com.gredicer.camerastudy.databinding.ActivityMainBinding

class MainActivity : BasePermissionActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkPermissions(NEEDED_PERMISSIONS)) initView()
        else ActivityCompat.requestPermissions(
            this,
            NEEDED_PERMISSIONS,
            ACTION_REQUEST_PERMISSIONS
        )

    }

    override fun afterRequestPermission(requestCode: Int, isAllGranted: Boolean) {
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            if (isAllGranted) initView() else showToast("存在未同意的权限")
        }
    }


    /** 初始化 UI */
    private fun initView() {
        binding.btnCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    companion object {
        /** 所需的所有权限信息*/
        private val NEEDED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)
        private const val ACTION_REQUEST_PERMISSIONS = 0x001
    }
}