package com.gredicer.videostudy.ui

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.LinearLayout
import android.widget.MediaController
import com.gredicer.videostudy.base.BaseBindingActivity
import com.gredicer.videostudy.databinding.ActivityMultiVideoBinding
import java.io.IOException

class MultiVideoActivity : BaseBindingActivity<ActivityMultiVideoBinding>(), MediaController.MediaPlayerControl,
    MediaPlayer.OnBufferingUpdateListener, SurfaceHolder.Callback {
    private var mediaPlayer: MediaPlayer? = null
    private var controller: MediaController? = null
    private var bufferPercentage = 0
    private var videoPath = ""
    private var videoSuf: SurfaceView? = null
    private var videoPaths = arrayListOf(
        "/storage/emulated/0/Pictures/MyCameraApp/VID_20220311_172702.mp4",
        "/storage/emulated/0/Pictures/MyCameraApp/VID_20220311_172500.mp4"
    )
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoPath = "https://cdn.cnbj1.fds.api.mi-img.com/miui-13/connect/connect-mobile-landing_27.mp4"
        mediaPlayer = MediaPlayer()
        controller = MediaController(this)
        controller!!.setAnchorView(binding.rootLl)
        initSurfaceView()
        binding.test.setOnClickListener {
            index++
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer!!.setDataSource(videoPaths[0])
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                changeVideoSize()
                // 然后开始播放视频
                mediaPlayer!!.start()
            }
        }
    }

    private fun initSurfaceView() {
        videoSuf = binding.controllerSurfaceView
        videoSuf!!.setZOrderOnTop(false)
        videoSuf!!.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        videoSuf!!.holder.addCallback(this)
    }

    override fun onResume() {
        super.onResume()
        try {
            mediaPlayer!!.setDataSource(videoPaths[index])
            mediaPlayer!!.setOnBufferingUpdateListener(this)
            // mediaPlayer.prepare();
//            controller!!.setMediaPlayer(this)
//            controller!!.isEnabled = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (null != mediaPlayer) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        controller!!.show()
        return super.onTouchEvent(event)
    }

    // MediaPlayer
    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    // MediaPlayerControl
    override fun onBufferingUpdate(mediaPlayer: MediaPlayer?, i: Int) {
        bufferPercentage = i
    }

    override fun start() {
        if (null != mediaPlayer) {
            mediaPlayer!!.start()
        }
    }

    override fun pause() {
        if (null != mediaPlayer) {
            mediaPlayer!!.pause()
        }
    }

    override fun getDuration(): Int {
        return mediaPlayer!!.duration
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer!!.currentPosition
    }

    override fun seekTo(i: Int) {
        mediaPlayer!!.seekTo(i)
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    override fun getBufferPercentage(): Int {
        return bufferPercentage
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    // SurfaceHolder.callback
    override fun surfaceCreated(p0: SurfaceHolder) {
        mediaPlayer!!.setDisplay(p0)
        mediaPlayer!!.prepareAsync()
        mediaPlayer!!.setOnPreparedListener {
            changeVideoSize()
            // 然后开始播放视频
            mediaPlayer!!.start()
        }
    }

    /**
     * 用来适配视频尺寸
     */
    private fun changeVideoSize() {
        var videoWidth = mediaPlayer!!.videoWidth
        var videoHeight = mediaPlayer!!.videoHeight

        // 根据视频尺寸去计算->视频可以在sufaceView中放大的最大倍数。
        val max: Float = if (resources.configuration.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            // 竖屏模式下按视频宽度计算放大倍数值
            (videoWidth.toFloat() / videoSuf?.width!!).coerceAtLeast(videoHeight.toFloat() / videoSuf?.height!!)
        } else {
            // 横屏模式下按视频高度计算放大倍数值
            (videoWidth.toFloat() / videoSuf?.height!!).coerceAtLeast(videoHeight.toFloat() / videoSuf?.width!!)
        }

        // 视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = kotlin.math.ceil((videoWidth.toFloat() / max).toDouble()).toInt()
        videoHeight = kotlin.math.ceil((videoHeight.toFloat() / max).toDouble()).toInt()

        // 无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView 让视频自动填充。
        videoSuf!!.layoutParams = LinearLayout.LayoutParams(videoWidth, videoHeight)
    }

    override fun surfaceChanged(p0: SurfaceHolder, i: Int, i1: Int, i2: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder) {}
}