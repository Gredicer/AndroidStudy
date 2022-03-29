package com.gredicer.videoedit.ui

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.ImageReader
import android.media.MediaCodec
import android.media.MediaCodec.CodecException
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.gredicer.videoedit.base.BaseBindingActivity
import com.gredicer.videoedit.databinding.ActivityEditBinding
import com.gredicer.videoedit.util.YuvToRgbConverter
import java.nio.ByteBuffer
import kotlin.math.abs


class EditActivity : BaseBindingActivity<ActivityEditBinding>() {

    // 获取视频每帧图像
    private var videoBitmaps = mutableListOf<Bitmap>()

    private var mediaExtractor = MediaExtractor()

    private var videoFormat = MediaFormat()
    private var mediaCodec: MediaCodec? = null

    private var rotation = 0
    private var duration = 0L
    private var totalCount = 0
    private var fullWidth = 0
    private var fullHeight = 0

    private var i = 0

    private var startTime = System.currentTimeMillis()
    private var endTime = System.currentTimeMillis()

    private var count = 0

    private val filePath1 = "/storage/emulated/0/1/VID_20220309_142148.mp4"
//    private val filePath2 = "/storage/emulated/0/1/result.mp4"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // 设置视频资源 MediaExtractor，获取资源轨道数量
        mediaExtractor.setDataSource(filePath1)
        val numTracks = mediaExtractor.trackCount
        // 遍历轨道，获取到 video 轨道的格式
        for (i in 0..numTracks) {
            val trackFormat = mediaExtractor.getTrackFormat(i)
            if (trackFormat.getString(MediaFormat.KEY_MIME)?.contains("video") == true) {
                videoFormat = trackFormat
                mediaExtractor.selectTrack(i)
                break;
            }
        }

        // 指定帧率
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 1);
        // 指定比特率
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 10000000);
        // 指定关键帧时间间隔，一般设置为每秒关键帧
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        // 指定编码器颜色格式，下面这三个必须要设置，不设置的话会导致错误：catch exception:android.media.MediaCodec$CodecException: Error 0xfffffc0e
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FormatYUV420Flexible)
        // 指定宽高
        fullWidth = videoFormat.getInteger(MediaFormat.KEY_WIDTH)
        if (videoFormat.containsKey("crop-left") && videoFormat.containsKey("crop-right")) {
            fullWidth = videoFormat.getInteger("crop-right") + 1 - videoFormat.getInteger("crop-left")
        }

        fullHeight = videoFormat.getInteger(MediaFormat.KEY_HEIGHT)
        if (videoFormat.containsKey("crop-top") && videoFormat.containsKey("crop-bottom")) {
            fullHeight = videoFormat.getInteger("crop-bottom") + 1 - videoFormat.getInteger("crop-top")
        }
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, fullWidth)
        videoFormat.setInteger(MediaFormat.KEY_HEIGHT, fullHeight)
        // 获取视频方向
        rotation =
            if (videoFormat.containsKey(MediaFormat.KEY_ROTATION)) videoFormat.getInteger(MediaFormat.KEY_ROTATION)
            else 0
        // 视频总时间(us)，备注：1毫秒(ms) = 1000微秒(us)
        duration = videoFormat.getLong(MediaFormat.KEY_DURATION)
        totalCount = (duration / 1000 / 1000).toInt()
        LogUtils.e("asd", "视频总时间", totalCount)

        mediaCodec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME).toString())
        mediaCodec!!.setCallback(codecCallback)
        val imageReader = ImageReader.newInstance(
            videoFormat.getInteger(MediaFormat.KEY_WIDTH),
            videoFormat.getInteger(MediaFormat.KEY_HEIGHT),
            ImageFormat.YUV_420_888, 3
        )
        imageReader.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
//                val bmp = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
//                val yuvToRgbConverter = YuvToRgbConverter(this)
//                yuvToRgbConverter.yuvToRgb(image, bmp)
//                videoBitmaps.add(bmp)
                LogUtils.e("asd1", count++)
                //一定要close否则buff溢出
                image.close()
            }
        }, null)





        binding.surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                mediaCodec!!.configure(videoFormat, binding.surfaceView.holder.surface, null, 0)
                mediaCodec!!.start()
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {
            }

        })

//        binding.test.setOnClickListener {
//            LogUtils.e("asd", videoBitmaps.size)
//            val index = i % videoBitmaps.size
//            binding.frameShow.setImageBitmap(videoBitmaps[index])
//            i++
//        }
    }


    private var codecCallback: MediaCodec.Callback = object : MediaCodec.Callback() {
        private var i = 0

        // input队列和output队列是同时进行的
        override fun onInputBufferAvailable(mediaCodec: MediaCodec, inputBufferId: Int) {
            val inputBuffer: ByteBuffer = mediaCodec.getInputBuffer(inputBufferId) ?: return
            val size: Int = mediaExtractor.readSampleData(inputBuffer, 0)
            if (size < 0) {
                mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                endTime = System.currentTimeMillis()
                LogUtils.e("zxc", "方法使用时间：${(endTime - startTime).toFloat() / 1000} s", "总次数$i")
            } else {
                i++
                // 将此帧加入队列
                mediaCodec.queueInputBuffer(inputBufferId, 0, size, mediaExtractor.sampleTime, 0)
                // 进入下一帧
                mediaExtractor.advance()
            }
        }

        override fun onOutputBufferAvailable(mediaCodec: MediaCodec, outputBufferId: Int, info: MediaCodec.BufferInfo) {
            // 释放 buffer 并传到 imageReader 里
            mediaCodec.releaseOutputBuffer(outputBufferId, true)
        }

        override fun onError(mediaCodec: MediaCodec, e: CodecException) {}
        override fun onOutputFormatChanged(mediaCodec: MediaCodec, format: MediaFormat) {}
    }


}


