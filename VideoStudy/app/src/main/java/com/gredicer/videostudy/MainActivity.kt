package com.gredicer.videostudy

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import com.daimajia.easing.Glider
import com.daimajia.easing.Skill
import com.gredicer.videostudy.base.BaseBindingActivity
import com.gredicer.videostudy.databinding.ActivityMainBinding
import com.gredicer.videostudy.ui.SurfaceViewActivity
import com.gredicer.videostudy.ui.VideoViewActivity


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnVideoView.setOnClickListener { startActivity(Intent(this, VideoViewActivity::class.java)) }
        binding.btnSurfaceView.setOnClickListener { startActivity(Intent(this, SurfaceViewActivity::class.java)) }

        binding.test.setOnClickListener {
            val set = AnimatorSet()
            set.playTogether(
                Glider.glide(
                    Skill.CircEaseOut,
                    1110f,
                    ObjectAnimator.ofFloat(binding.test, "translationY", 0f, 500f)
                )
            )

            set.duration = 1500
            set.start()
        }
    }
}