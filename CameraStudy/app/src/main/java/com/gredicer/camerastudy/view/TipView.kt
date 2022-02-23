package com.gredicer.camerastudy.view

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.widget.RelativeLayout
import com.gredicer.camerastudy.R


class TipView(context: Context?) : RelativeLayout(context) {

    private var mMaskBitmap: Bitmap? = null
    private var mMaskCanvas: Canvas? = null
    private var mMaskPaint: Paint? = null

    private var mTransparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPorterDuffXfermode: PorterDuffXfermode? = null

    private var mSystemCanvasPaint: Paint? = null

    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.person)

    init {
        setWillNotDraw(false)
        mMaskPaint = Paint()
        mMaskPaint = Paint()
        mMaskPaint!!.color = 0xC8000000.toInt()

        mTransparentPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTransparentPaint.color = Color.BLACK
        mPorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        mTransparentPaint.xfermode = mPorterDuffXfermode

        mSystemCanvasPaint = Paint()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 绘制遮罩层
        if (mMaskBitmap == null) {
            mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            mMaskCanvas = Canvas(mMaskBitmap!!)
        }

        mMaskCanvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mMaskPaint!!)
        // 在遮罩层上挖一个洞
//        mMaskCanvas?.drawCircle(180f, 230f, 100f, mTransparentPaint);

        // 获取屏幕宽高
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        // 获取左上角画笔起始点
        val startLeft = (width - bitmap.width).toFloat() / 2
        val startTop = (height - bitmap.height).toFloat() / 2
        // 绘制
        mMaskCanvas?.drawBitmap(bitmap, startLeft, startTop, mTransparentPaint)
        canvas!!.drawBitmap(mMaskBitmap!!, 0f, 0f, mSystemCanvasPaint)

    }
}