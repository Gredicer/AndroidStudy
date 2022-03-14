package com.gredicer.videoedit.util

import android.graphics.Bitmap


interface MediaCodeFrameCallBack {
    fun onProgress(totalCount: Int, currentCount: Int)
    fun onFrameBitmap(bitmap: Bitmap?)
    fun onFrameFinish()
}