package com.gredicer.videostudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.VideoView
import com.gredicer.videostudy.base.BaseBindingActivity
import com.gredicer.videostudy.databinding.ActivityMainBinding

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.videoView.setVideoPath("https://cdn.cnbj1.fds.api.mi-img.com/miui-13/connect/connect-mobile-landing_27.mp4")
        binding.videoView.start()
    }
}