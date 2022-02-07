package com.gredicer.videostudy.ui

import android.os.Bundle
import com.gredicer.videostudy.base.BaseBindingActivity
import com.gredicer.videostudy.databinding.ActivityVideoViewBinding

class VideoViewActivity : BaseBindingActivity<ActivityVideoViewBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.videoView.setVideoPath("https://cdn.cnbj1.fds.api.mi-img.com/miui-13/connect/connect-mobile-landing_27.mp4")
        binding.videoView.start()
    }
}