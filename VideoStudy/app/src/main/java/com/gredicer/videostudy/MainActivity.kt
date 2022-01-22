package com.gredicer.videostudy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.VideoView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val videoView = findViewById<VideoView>(R.id.videoView)
        videoView.setVideoPath("https://cdn.cnbj1.fds.api.mi-img.com/miui-13/connect/connect-mobile-landing_27.mp4")
        videoView.start()
    }
}