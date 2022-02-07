package com.gredicer.videostudy

import android.content.Intent
import android.os.Bundle
import com.gredicer.videostudy.base.BaseBindingActivity
import com.gredicer.videostudy.databinding.ActivityMainBinding
import com.gredicer.videostudy.ui.VideoViewActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnVideoView.setOnClickListener { startActivity(Intent(this, VideoViewActivity::class.java)) }
    }
}