package com.gredicer.videoedit.ui

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import com.gredicer.videoedit.base.BaseBindingActivity
import com.gredicer.videoedit.databinding.ActivityTestBinding
import com.gredicer.videoedit.util.MediaCodeFrameCallBack
import com.gredicer.videoedit.util.MediaCodeFrameUtil
import com.gredicer.videoedit.util.TestAdapter


class TestActivity : BaseBindingActivity<ActivityTestBinding>(), MediaCodeFrameCallBack {
    private val list: ArrayList<Bitmap> = ArrayList()
    private var mediaCodeFrameUtil: MediaCodeFrameUtil? = null
    private var startTime = System.currentTimeMillis()
    private var mAdapter = TestAdapter()

    // 当前RecyclerView的横向位置
    private var totalDx = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        mediaCodeFrameUtil = MediaCodeFrameUtil(PATH, this)
        mediaCodeFrameUtil?.decode()

//        binding.test.setOnClickListener {
//            LogUtils.e("asd", list.size, list[0].height, list[0].width)
//        }

        binding.rvTest.apply {
            val mLayoutManager = LinearLayoutManager(this@TestActivity)
            mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = mLayoutManager
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    totalDx += dx
                    LogUtils.e("asd", totalDx, dx)
                }
            })
        }
    }


    override fun onProgress(totalCount: Int, currentCount: Int) {
        LogUtils.e("asd", "totalCount = $totalCount   currentCount = $currentCount")
    }

    override fun onFrameBitmap(bitmap: Bitmap?) {
        LogUtils.e("qwe")
        if (bitmap != null) {
            list.add(bitmap)
            mAdapter.updateList(list)
        }
    }

    override fun onFrameFinish() {
        LogUtils.e("asd", "onFrameFinish", "总时间：${(System.currentTimeMillis() - startTime).toFloat() / 1000}")

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDestroy() {
        super.onDestroy()
        mediaCodeFrameUtil?.stopDecode()
    }

    companion object {
        val PATH: String = "/storage/emulated/0/DCIM/Camera/VID_20220315_163916.mp4"
    }

}

