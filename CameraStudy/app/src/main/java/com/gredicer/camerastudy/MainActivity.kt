package com.gredicer.camerastudy

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.RegexUtils
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

        LogUtils.e("asd", RegexUtils.getMatches("测试", "测试一下"))

        binding.btnCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
        binding.btnRemainWord.setOnClickListener {
            binding.textScrollView.run {
                mDuration = (content.length / binding.sbSpeed.progress * 1000).toLong()
                mTextSize = binding.sbTextSize.progress.toFloat()
                animateProgress()
            }
        }


        binding.textScrollView.content = "一测试文字\n 二测试文字 \n 三测试文字 \n 四测试文字 \n"

        binding.sbSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

                binding.textScrollView.run {
                    Log.e("asd", "onProgressChanged: ${content.length}, ${p1}")
                    mDuration = (content.length / p1 * 1000).toLong()
                    invalidate()
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.sbTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.textScrollView.mTextSize = p1.toFloat()
                binding.textScrollView.invalidate()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

    }

    companion object {
        /** 所需的所有权限信息*/
        private val NEEDED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE)
        private const val ACTION_REQUEST_PERMISSIONS = 0x001
    }
}