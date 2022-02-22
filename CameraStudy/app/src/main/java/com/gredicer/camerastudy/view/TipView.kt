package com.gredicer.camerastudy.view

import android.content.Context
import android.graphics.*
import android.widget.RelativeLayout


class TipView(context: Context?) : RelativeLayout(context) {

    private var mMaskBitmap: Bitmap? = null
    private var mMaskCanvas: Canvas? = null
    private var mMaskPaint: Paint? = null

    private var mTransparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPorterDuffXfermode: PorterDuffXfermode? = null

    private var mSystemCanvasPaint: Paint? = null

    init {
        setWillNotDraw(false)
        mMaskPaint = Paint()
        mMaskPaint = Paint()
        mMaskPaint!!.color = 0xC8000000.toInt()

        mTransparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTransparentPaint.color = Color.TRANSPARENT
        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mTransparentPaint.xfermode = mPorterDuffXfermode

        mSystemCanvasPaint = Paint()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 绘制遮罩层
        if (mMaskBitmap == null) {
            mMaskBitmap = Bitmap.createBitmap(
                width, height, Bitmap.Config.ARGB_8888
            );
            mMaskCanvas = Canvas(mMaskBitmap!!)
        }

        mMaskCanvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mMaskPaint!!)
        // 在遮罩层上挖一个洞
        mMaskCanvas?.drawCircle(180f, 230f, 100f, mTransparentPaint);
        canvas!!.drawBitmap(mMaskBitmap!!, 0f, 0f, mSystemCanvasPaint)

    }
}