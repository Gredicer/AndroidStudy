package com.gredicer.videoedit.util

import android.graphics.*
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import io.github.crow_misia.libyuv.*
import kotlinx.coroutines.Runnable


@RequiresApi(Build.VERSION_CODES.M)
class MediaCodeFrameUtil(path: String?, callBack: MediaCodeFrameCallBack?) : Runnable {
    private var videoPath: String? = null
    private var extractor: MediaExtractor?
    private var videoFormat: MediaFormat = MediaFormat()
    private var rotation = 0
    private val duration: Long
    private var mediaCodec: MediaCodec?
    private var childThread: Thread? = null
    private val mMediaCodeFrameCallBack: MediaCodeFrameCallBack?


    @Volatile
    private var count = 0 //子线程里面变化数据，要用volatile

    @Volatile
    private var progressCount = 0

    @Volatile
    private var totalCount = 0
    private var handler: Handler? = null
    private var stopDecode = false

    init {
        videoPath = path
        mMediaCodeFrameCallBack = callBack
        handler = Handler(Looper.getMainLooper())
        extractor = MediaExtractor()
        extractor!!.setDataSource(videoPath!!)
        val trackCount = extractor!!.trackCount
        for (x in 0 until trackCount) {
            val trackFormat = extractor!!.getTrackFormat(x)
            if (trackFormat.getString(MediaFormat.KEY_MIME)!!.contains("video")) {
                videoFormat = trackFormat
                extractor!!.selectTrack(x)
                break
            }
        }
        if (videoFormat == null) throw Exception("没有发现视频关键帧")
        videoFormat.setInteger(
            MediaFormat.KEY_COLOR_FORMAT, COLOR_FormatYUV420Flexible
        )
        videoFormat.setInteger(MediaFormat.KEY_WIDTH, videoFormat.getInteger(MediaFormat.KEY_WIDTH))
        videoFormat.setInteger(MediaFormat.KEY_HEIGHT, videoFormat.getInteger(MediaFormat.KEY_HEIGHT))

        if (videoFormat.containsKey(MediaFormat.KEY_ROTATION)) {
            rotation = videoFormat.getInteger(MediaFormat.KEY_ROTATION)
        }
        duration = videoFormat.getLong(MediaFormat.KEY_DURATION)
        totalCount = (duration / 1000 / 1000).toInt()
        mediaCodec = MediaCodec.createDecoderByType(videoFormat.getString(MediaFormat.KEY_MIME)!!)
        mediaCodec!!.configure(videoFormat, null, null, 0)

        // 获取视频对应帧格式
        val mediaFormat: MediaFormat = mediaCodec!!.outputFormat
        when (mediaFormat.getInteger(MediaFormat.KEY_COLOR_FORMAT)) {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411Planar -> {}
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV411PackedPlanar -> {}
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar -> {}
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar -> {}
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar -> {}
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar -> {}
            else -> {}
        }
    }

    fun decode() {
        if (childThread == null) {
            stopDecode = false //开始后，停止解析标签为false
            childThread = Thread(this, "decode")
            childThread!!.start()
        }
    }

    fun stopDecode() {
        if (childThread != null) {
            stopDecode = true //子线程调用stop没法停止线程的，我们用了while循环，所以做个标志位，提示跳出while循环
            childThread = null
        }
    }

    override fun run() {
        if (mediaCodec == null) return
        mediaCodec!!.start()

        count = 0
        progressCount = 0
        val bufferInfo = MediaCodec.BufferInfo()
        val timeOut = (5 * 1000).toLong()
        var width = 0
        var height = 0

        while (progressCount <= totalCount) {
            val inputBufferId: Int = mediaCodec!!.dequeueInputBuffer(timeOut)
            if (inputBufferId >= 0) {
                extractor!!.seekTo((count * 1000 * 1000).toLong(), MediaExtractor.SEEK_TO_CLOSEST_SYNC)
                val inputBuffer = mediaCodec!!.getInputBuffer(inputBufferId)
                val sampleData = extractor!!.readSampleData(inputBuffer!!, 0)
                if (sampleData > 0 && count <= totalCount) {
                    val sampleTime = extractor!!.sampleTime
                    mediaCodec!!.queueInputBuffer(inputBufferId, 0, sampleData, sampleTime, 0)
                    extractor!!.advance()
                    count++
                } else {
                    mediaCodec!!.queueInputBuffer(inputBufferId, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                }
            }
            val outputBufferId = mediaCodec!!.dequeueOutputBuffer(bufferInfo, timeOut)
            if (outputBufferId >= 0) {
                val outputBuffer = mediaCodec!!.getOutputBuffer(outputBufferId)


//                val outputFormat = mediaCodec!!.getOutputFormat(outputBufferId)
//                val image = mediaCodec!!.getOutputImage(outputBufferId)
//                when (image!!.format) {
//                    ImageFormat.YUV_420_888 -> {
//                        val i420Buffer = I420Buffer.wrap(outputBuffer!!, width, height)
//                        val i420ScaleBuffer = I420Buffer.allocate(200, 200 * height / width)
//                        i420Buffer.scale(i420ScaleBuffer, FilterMode.BILINEAR)
//                        i420Buffer.close()
//                        handler!!.post { mMediaCodeFrameCallBack?.onFrameBitmap(i420ScaleBuffer.asBitmap()) }
//                        i420ScaleBuffer.close()
//                    }
//                    ImageFormat.NV21 -> {
//                        val nv21Buffer = Nv21Buffer.wrap(outputBuffer!!, width, height)
//                        val nv21ScaleBuffer = Nv21Buffer.allocate(200, 200 * height / width)
//                        nv21Buffer.scale(nv21ScaleBuffer, FilterMode.BILINEAR)
//                        nv21Buffer.close()
//                        handler!!.post { mMediaCodeFrameCallBack?.onFrameBitmap(nv21ScaleBuffer.asBitmap()) }
//                        nv21ScaleBuffer.close()
//                    }
//                    ImageFormat.YV12 -> {
//                        val nv12Buffer = Nv12Buffer.wrap(outputBuffer!!, width, height)
//                        val nv12ScaleBuffer = Nv12Buffer.allocate(200, 200 * height / width)
//                        nv12Buffer.scale(nv12ScaleBuffer, FilterMode.BILINEAR)
//                        nv12Buffer.close()
//                        handler!!.post { mMediaCodeFrameCallBack?.onFrameBitmap(nv12ScaleBuffer.asBitmap()) }
//                        nv12ScaleBuffer.close()
//                    }
//                }


//                val nv21Buffer = Nv21Buffer.wrap(outputBuffer!!, width, height)
//                val nv21ScaleBuffer = Nv21Buffer.allocate(200, 200 * height / width)
//                nv21Buffer.scale(nv21ScaleBuffer, FilterMode.BILINEAR)
//                nv21Buffer.close()
//                handler!!.post { mMediaCodeFrameCallBack?.onFrameBitmap(nv21ScaleBuffer.asBitmap()) }
//                nv21ScaleBuffer.close()

                val nv12Buffer = Nv12Buffer.wrap(outputBuffer!!, width, height)
                val nv12ScaleBuffer = Nv12Buffer.allocate(200, 200 * height / width)
                nv12Buffer.scale(nv12ScaleBuffer, FilterMode.BILINEAR)
                nv12Buffer.close()
                handler!!.post { mMediaCodeFrameCallBack?.onFrameBitmap(nv12ScaleBuffer.asBitmap()) }
                nv12ScaleBuffer.close()

//                image!!.close()
                mediaCodec!!.releaseOutputBuffer(outputBufferId, true)
                mMediaCodeFrameCallBack?.onProgress(totalCount, progressCount)
                progressCount++
            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                val outputFormat = mediaCodec!!.outputFormat
                width = outputFormat.getInteger(MediaFormat.KEY_WIDTH)
                height = outputFormat.getInteger(MediaFormat.KEY_HEIGHT)
            }
        }
        mediaCodec!!.stop()
        mediaCodec!!.release()
        mediaCodec = null

        mMediaCodeFrameCallBack?.onFrameFinish()
    }
}