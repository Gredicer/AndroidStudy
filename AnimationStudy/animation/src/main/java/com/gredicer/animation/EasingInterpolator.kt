package com.gredicer.animation

import android.view.animation.Interpolator
import kotlin.math.abs
import kotlin.math.pow

class EasingInterpolator(var type: EaseType) : Interpolator {

    override fun getInterpolation(fraction: Float): Float = get(type, fraction)

    fun get(easeType: EaseType, fraction: Float): Float {
        return when (easeType) {
            EaseType.Linear -> fraction
            EaseType.InQuad -> powIn(fraction, 2)
            EaseType.OutQuad -> powOut(fraction, 2)
            EaseType.InOutQuad -> powInOut(fraction, 2)
            EaseType.InCubic -> powIn(fraction, 3)
            EaseType.OutCubic -> powOut(fraction, 3)
            EaseType.InOutCubic -> powInOut(fraction, 3)
            EaseType.InQuart -> powIn(fraction, 4)
            EaseType.OutQuart -> powOut(fraction, 4)
            EaseType.InOutQuart -> powInOut(fraction, 4)
            EaseType.InQuint -> powIn(fraction, 5)
            EaseType.OutQuint -> powOut(fraction, 5)
            EaseType.InOutQuint -> powInOut(fraction, 5)
            else -> 0f
        }
    }


    private fun powIn(fraction: Float, num: Int) = fraction.pow(num)
    private fun powOut(fraction: Float, num: Int): Float = 1 - (1 - fraction).pow(num)
    private fun powInOut(fraction: Float, num: Int): Float {
        val f = fraction * 2
        return if (f < 1) (0.5 * f.pow(num)).toFloat()
        else (1 - 0.5 * abs((2 - f).pow(num))).toFloat()
    }


}