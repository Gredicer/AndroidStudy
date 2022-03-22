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
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.LogUtils
import com.gredicer.videoedit.base.BaseBindingActivity
import com.gredicer.videoedit.databinding.ActivityEditBinding
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

    private var i = 0

    private var startTime = System.currentTimeMillis()
    private var endTime = System.currentTimeMillis()

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
//        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 1);
//        // 指定比特率
//        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, 10000000);
        // 指定关键帧时间间隔，一般设置为每秒关键帧
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
        // 指定编码器颜色格式，下面这三个必须要设置，不设置的话会导致错误：catch exception:android.media.MediaCodec$CodecException: Error 0xfffffc0e
        videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, COLOR_FormatYUV420Flexible)
        // 指定宽高
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, videoFormat.getInteger(MediaFormat.KEY_WIDTH) / 4)
        videoFormat.setInteger(MediaFormat.KEY_HEIGHT, videoFormat.getInteger(MediaFormat.KEY_HEIGHT) / 4)
        // 获取视频方向
//        rotation = if (videoFormat.containsKey(MediaFormat.KEY_ROTATION))
//            videoFormat.getInteger(MediaFormat.KEY_ROTATION) else 0
        // 视频总时间(us)，备注：1毫秒(ms) = 1000微秒(us)
        duration = videoFormat.getLong(MediaFormat.KEY_DURATION)
        totalCount = (duration / 1000 / 1000).toInt()
        LogUtils.e("asd", totalCount)

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
                val height = image.height
                val width = image.width
//                LogUtils.e("asd", height, width)


//
//                val bmp = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
//
//
//                val yuvToRgbConverter = YuvToRgbConverter(this)
//                yuvToRgbConverter.yuvToRgb(image, bmp)
//
//                videoBitmaps.add(bmp)
                LogUtils.e("asd1")
//                val bitmap: Bitmap = imageToBitmap(image)
//                val bytes: ByteArray = bitmapToByteArray(bitmap)
                //一定要close否则buff溢出
                image.close()
            }
        }, null)


        mediaCodec!!.configure(videoFormat, imageReader.surface, null, 0)
        mediaCodec!!.start()


        binding.test.setOnClickListener {
            LogUtils.e("asd", videoBitmaps.size)
            val index = i % videoBitmaps.size
            binding.frameShow.setImageBitmap(videoBitmaps[index])
            i++
        }
    }


    private var codecCallback: MediaCodec.Callback = object : MediaCodec.Callback() {
        private var i = 0
        private var count = 0


        // input队列和output队列是同时进行的
        // 总共有6个通道，不同手机可能不一样

        override fun onInputBufferAvailable(mediaCodec: MediaCodec, inputBufferId: Int) {

            val inputBuffer: ByteBuffer = mediaCodec.getInputBuffer(inputBufferId) ?: return
            val size: Int = mediaExtractor.readSampleData(inputBuffer, 0)

            LogUtils.e(
                "asd",
                "当前时间：${totalCount}",
                "当前秒数：${count}"
            )

//            LogUtils.e("asd-Size", size, mediaExtractor.sampleTime)
            if (size < 0) {
                mediaCodec.queueInputBuffer(inputBufferId, 0, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                endTime = System.currentTimeMillis()
                LogUtils.e("zxc", "方法使用时间：${(endTime - startTime).toFloat() / 1000} s", "总次数$i")
            } else {
//                mediaExtractor.seekTo((count * 1000 * 1000).toLong(), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                // 将此帧加入队列
                mediaCodec.queueInputBuffer(inputBufferId, 0, size, ((count - 1) * 1000 * 1000).toLong(), 0)
                // 进入下一帧
                mediaExtractor.advance()
                val time = mediaExtractor.sampleTime
                LogUtils.e(
                    "asd",
                    "当前时间：${time}",
                    "时间差值：${abs(time - (count * 1000 * 1000))}",
                    "inputBufferId：${inputBufferId}",
                    "当前秒数：${count}"
                )
            }
        }

        override fun onOutputBufferAvailable(mediaCodec: MediaCodec, outputBufferId: Int, info: MediaCodec.BufferInfo) {

            LogUtils.e("qwe", "outputBufferId：${outputBufferId}", "视频帧时间：${info.presentationTimeUs}", info.size)
            mediaCodec.releaseOutputBuffer(outputBufferId, true)
            // 获取输出缓冲(其中包含编解码后数据)
//            val outputBuffer: ByteBuffer? = mediaCodec.getOutputBuffer(index)
//            val bufferFormat = mediaCodec.getOutputFormat(index)


            // true: Render the buffer with the default timestamp
//            mediaCodec.releaseOutputBuffer(index, true)
        }

        override fun onError(mediaCodec: MediaCodec, e: CodecException) {}
        override fun onOutputFormatChanged(mediaCodec: MediaCodec, format: MediaFormat) {}
    }


}


