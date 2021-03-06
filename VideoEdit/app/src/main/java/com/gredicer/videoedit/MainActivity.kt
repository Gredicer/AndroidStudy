package com.gredicer.videoedit

import android.content.Intent
import android.os.Bundle
import com.gredicer.videoedit.base.BaseBindingActivity
import com.gredicer.videoedit.databinding.ActivityMainBinding
import com.gredicer.videoedit.ui.EditActivity
import com.gredicer.videoedit.ui.TestActivity
import com.gredicer.videoedit.util.GlideEngine
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.toast.ToastUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import io.microshow.rxffmpeg.RxFFmpegInvoke
import io.microshow.rxffmpeg.RxFFmpegSubscriber


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    private var myRxFFmpegSubscriber: MyRxFFmpegSubscriber? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.test.setOnClickListener { runFFmpegRxJava() }

        XXPermissions.with(this)
            .permission(Permission.Group.STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {

                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {}
            })



        binding.btnGetPicture.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>?) {}
                    override fun onCancel() {}
                })
        }

        binding.btnEdit.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }

        binding.btnTest.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }


    private fun runFFmpegRxJava() {
        val text =
            "ffmpeg -y -i /storage/emulated/0/FoowwVideo/edit/2022-02-24/1645667349003.mp4 -vf boxblur=25:5 -preset superfast /storage/emulated/0/1/result1.mp4"
        val commands = text.split(" ").toTypedArray()
        myRxFFmpegSubscriber = MyRxFFmpegSubscriber()

        //????????????FFmpeg??????
        RxFFmpegInvoke.getInstance()
            .runCommandRxJava(commands)
            .subscribe(myRxFFmpegSubscriber)
    }


    class MyRxFFmpegSubscriber() : RxFFmpegSubscriber() {
        override fun onFinish() {
            ToastUtils.show("????????????")
        }

        override fun onProgress(progress: Int, progressTime: Long) {
            ToastUtils.show("?????????")
        }

        override fun onCancel() {
            ToastUtils.show("?????????")
        }

        override fun onError(message: String) {
            ToastUtils.show("????????? onError???$message")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myRxFFmpegSubscriber?.dispose()
    }
}