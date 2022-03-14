package com.gredicer.camerastudy.view

import android.R.id.text1
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class TextScrollView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    private var currentPercentage = 0
    var mTextSize = 0f
    var mDuration = 10000L
    var content = ""
    lateinit var staticLayout1: StaticLayout

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.DKGRAY)


        val textPaint = TextPaint().apply {
            textSize = mTextSize + 50f
            color = Color.WHITE
        }

        staticLayout1 = StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true)

        canvas?.translate(0f, -currentPercentage.toFloat())
        canvas?.save()
        staticLayout1.draw(canvas)
        canvas?.restore()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

    }

//    fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
//        scroller.fling(
//            currentX,
//            currentY,
//            (velocityX / SCALE).toInt(),
//            (velocityY / SCALE).toInt(),
//            minX,
//            minY,
//            maxX,
//            maxY
//        )
//        postInvalidate()
//        return true
//    }


    fun animateProgress() {
        currentPercentage = 0
        val valuesHolder = PropertyValuesHolder.ofFloat("percentage", 0f, 1000f)
        val animator = ValueAnimator().apply {
            setValues(valuesHolder)
            duration = mDuration
            addUpdateListener {
                val percentage = it.getAnimatedValue(PERCENTAGE_VALUE_HOLDER) as Float
                currentPercentage = percentage.toInt()
                invalidate()
            }
        }
        animator.start()
    }


    companion object {
        const val ARC_FULL_ROTATION_DEGREE = 360
        const val PERCENTAGE_DIVIDER = 100.0
        const val PERCENTAGE_VALUE_HOLDER = "percentage"
    }
}