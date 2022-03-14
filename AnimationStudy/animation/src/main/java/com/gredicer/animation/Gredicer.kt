package com.gredicer.animation

import android.animation.ValueAnimator

object Gredicer {

    var type: MyType = MyType.TEST

    private fun glide(
        duration: Float,
        animator: ValueAnimator?,
        vararg listeners: BaseAnimation.EasingListener?
    ): ValueAnimator? = animator?.apply {
        setEvaluator(BaseAnimation().apply {
            mDuration = duration
            addEasingListeners(*listeners)
        })
    }


    fun glide(duration: Float, animator: ValueAnimator?): ValueAnimator? = glide(duration, animator, *emptyArray())


//    fun glide(
//        skill: AnimationType,
//        duration: Float,
//        propertyValuesHolder: PropertyValuesHolder
//    ): PropertyValuesHolder {
//        propertyValuesHolder.setEvaluator(skill.getMethod(duration))
//        return propertyValuesHolder
//    }
}

enum class MyType { BackEaseIn, TEST; }