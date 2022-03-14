package com.example.animationstudy

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import com.example.animationstudy.base.BaseBindingActivity
import com.example.animationstudy.databinding.ActivityMainBinding
import com.gredicer.animation.EaseType
import com.gredicer.animation.EasingInterpolator


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.test.setOnClickListener {
//            AnimatorSet().apply {
//                play(Gredicer.glide(1000f, ObjectAnimator.ofFloat(it, "translationY", 0f, 100f)))
//                duration = 1200
//                start()
//            }


//            AnimatorSet().apply {
//                play(ObjectAnimator.ofFloat(it, "translationY", 0f, 300f))
//                interpolator = EasingInterpolator(EaseType.InOutQuint)
//                duration = 1200
//                start()
//            }


            AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(it, "translationY", 0f, 300f).apply { duration = 800 },
                    ObjectAnimator.ofFloat(it, "translationY", 300f, 600f).apply {
                        startDelay = 600
                        duration = 800
                    }
                )
                interpolator = EasingInterpolator(EaseType.InOutQuint)
                start()
            }

//            val valueAnimator = ValueAnimator.ofInt(0, 400)
//            valueAnimator.duration = 2000
//            valueAnimator.addUpdateListener { animation ->
//                val curValue = animation.animatedValue as Int
//                //通过layout 函数改变TextView 的位置，而layout函数在改变控件位置时是永久的，即通过更改left ，top，right，bottom这四个点的坐标来更改坐标位置，而不仅仅是视觉上画在哪个位置上
//                it.layout(curValue, curValue, it.width + curValue, it.height + curValue)
//            }
//            valueAnimator.start()
        }


        

    }

}