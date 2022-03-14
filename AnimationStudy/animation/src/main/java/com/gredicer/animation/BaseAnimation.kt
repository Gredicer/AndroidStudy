package com.gredicer.animation

import android.animation.TypeEvaluator
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

open class BaseAnimation : TypeEvaluator<Number> {
    //    private var s = 1.70158f
    private var s = 1200f
    var mDuration = 0f
    var type = EaseType.InSine
    private val mListeners: ArrayList<EasingListener> = ArrayList()


    interface EasingListener {
        fun on(time: Float, value: Float, start: Float, end: Float, duration: Float)
    }


    open fun addEasingListener(l: EasingListener?) {
        mListeners.add(l!!)
    }

    open fun addEasingListeners(vararg ls: EasingListener?) {
        for (l in ls) {
            mListeners.add(l!!)
        }
    }

    open fun removeEasingListener(l: EasingListener?) {
        mListeners.remove(l)
    }

    open fun clearEasingListeners() {
        mListeners.clear()
    }

    open fun setDuration(duration: Float) {
        mDuration = duration
    }


    /**
     * This function returns the result of linearly interpolating the start and end values,
     * with fraction representing the proportion between the start and end values.
     * The calculation is a simple parametric calculation:
     * result = x0 + t * (x1 - x0), where x0 is startValue, x1 is endValue, and t is fraction.
     *
     * @param fraction Float            The fraction from the starting to the ending values
     * @param startValue Number
     * @param endValue Number
     * @return Float
     */
    override fun evaluate(fraction: Float, startValue: Number, endValue: Number): Float {
        var t = mDuration * fraction
        val b = startValue.toFloat()
        val c = endValue.toFloat() - startValue.toFloat()
        val d = mDuration
        val result = when (type) {
            EaseType.Linear -> c * t / d + b
            EaseType.InSine -> -c * cos(t / d * (Math.PI / 2)).toFloat() + c + b
            EaseType.OutSine -> c * sin(t / d * (Math.PI / 2)).toFloat() + b
            EaseType.InOutSine -> -c / 2 * (cos(Math.PI * t / d).toFloat() - 1) + b
            EaseType.InQuad -> -c * d.let { t /= it; t } * t + b
            EaseType.OutQuad -> -c * d.let { t /= it; t } * (t - 2) + b
            EaseType.InOutQuad -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t + b else -c / 2 * (--t * (t - 2) - 1) + b
            EaseType.InCubic -> c * d.let { t /= it; t } * t * t + b
            EaseType.OutCubic -> c * ((t / d - 1.also { t = it.toFloat() }) * t * t + 1) + b
            EaseType.InOutCubic -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t * t + b else c / 2 * (2.let { t -= it; t } * t * t + 2) + b
            EaseType.InQuint -> c * d.let { t /= it; t } * t * t * t * t + b
            EaseType.OutQuint -> c * ((t / d - 1.also { t = it.toFloat() }) * t * t * t * t + 1) + b
            EaseType.InOutQuint -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t * t * t * t + b else c / 2 * (2.let { t -= it; t } * t * t * t * t + 2) + b
            EaseType.InExpo -> if (t == 0f) b else c * 2.0.pow((10 * (t / d - 1)).toDouble()).toFloat() + b
            EaseType.OutExpo -> if (t == d) b + c else c * (-2.0.pow((-10 * t / d).toDouble()).toFloat() + 1) + b
            EaseType.InOutExpo -> inoutExpoCalculate(t, b, c, d)
            EaseType.InCirc -> -c * (sqrt((1 - d.let { t /= it; t } * t).toDouble()).toFloat() - 1) + b
            EaseType.OutCirc -> c * sqrt((1 - (t / d - 1.also { t = it.toFloat() }) * t).toDouble()).toFloat() + b
            EaseType.InOutCirc -> return if (d / 2.let { t /= it; t } < 1) -c / 2 * (sqrt((1 - t * t).toDouble()).toFloat() - 1) + b else c / 2 * (sqrt(
                (1 - 2.let { t -= it; t } * t).toDouble()).toFloat() + 1) + b
            EaseType.InBack -> -c * d.let { t /= it; t } * t + b
            EaseType.OutBack -> -c * d.let { t /= it; t } * (t - 2) + b
            EaseType.InOutBack -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t + b else -c / 2 * (--t * (t - 2) - 1) + b
            EaseType.InElastic -> -c * d.let { t /= it; t } * t + b
            EaseType.OutElastic -> -c * d.let { t /= it; t } * (t - 2) + b
            EaseType.InOutElastic -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t + b else -c / 2 * (--t * (t - 2) - 1) + b
            EaseType.InBounce -> -c * d.let { t /= it; t } * t + b
            EaseType.OutBounce -> -c * d.let { t /= it; t } * (t - 2) + b
            EaseType.InOutBounce -> return if (d / 2.let { t /= it; t } < 1) c / 2 * t * t + b else -c / 2 * (--t * (t - 2) - 1) + b

            else -> 0f
        }
        for (l in mListeners) {
            l.on(t, result, b, c, d)
        }
        return result
    }


    private fun inoutExpoCalculate(mt: Float, mb: Float, mc: Float, md: Float): Float {
        var t = mt
        return if (t == 0f) mb
        else if (t == md) mb + mc
        else if (md / 2.let { t /= it; t } < 1) mc / 2 * 2.0.pow((10 * (t - 1)).toDouble()).toFloat() + mb
        else mc / 2 * (-2.0.pow((-10 * --t).toDouble()).toFloat() + 2) + mb
    }


}