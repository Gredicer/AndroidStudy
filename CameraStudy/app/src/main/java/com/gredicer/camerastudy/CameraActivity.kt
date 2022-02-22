package com.gredicer.camerastudy

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.concurrent.futures.await
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import com.blankj.utilcode.util.ScreenUtils
import com.gredicer.camerastudy.base.BaseBindingActivity
import com.gredicer.camerastudy.databinding.ActivityCameraBinding
import com.gredicer.camerastudy.extensions.getAspectRatio
import com.gredicer.camerastudy.extensions.getNameString
import com.gredicer.camerastudy.view.TipView
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : BaseBindingActivity<ActivityCameraBinding>() {

    data class CameraCapability(val camSelector: CameraSelector, val qualities: List<Quality>)

    /** 选择的相机 前置|后置 */
    private var cameraIndex = 0

    /** 相机尺寸和比例*/
    private var qualityIndex = 0

    /** 当前的比例  0 - 9:16   1 - 16:9   2 - 4:3 */
    private var customRatio = 0

    private val cameraCapabilities = mutableListOf<CameraCapability>()
    private lateinit var videoCapture: VideoCapture<Recorder>
    private lateinit var recordingState: VideoRecordEvent
    private var currentRecording: Recording? = null
    private var enumerationDeferred: Deferred<Unit>? = null
    private val mainThreadExecutor by lazy { ContextCompat.getMainExecutor(this) }
    private val captureLiveStatus = MutableLiveData<String>()

    enum class UIState {
        IDLE,               // 不录制视频的时候，所有控件都显示
        RECORDING,          // 正在录制的时候，只有暂停/停止 按钮显示
        FINALIZED,          // 完成录制的时候，所有控件都显示
        RECOVERY            // 未来可能会用到
    }

    /** 查询并且缓存相机功能，只运行一次*/
    init {
        enumerationDeferred = lifecycleScope.async {
            whenCreated {
                val provider = ProcessCameraProvider.getInstance(this@CameraActivity).await()
                provider.unbindAll()
                for (camSelector in arrayOf(
                    CameraSelector.DEFAULT_BACK_CAMERA, CameraSelector.DEFAULT_FRONT_CAMERA
                )) {
                    try {
                        // 这里仅是获取摄像机的信息去获取 capability，没有去绑定任何东西
                        if (provider.hasCamera(camSelector)) {
                            val camera = provider.bindToLifecycle(this@CameraActivity, camSelector)
                            QualitySelector.getSupportedQualities(camera.cameraInfo).filter {
                                listOf(
                                    Quality.UHD, Quality.FHD, Quality.HD, Quality.SD
                                ).contains(it)
                            }.also {
                                cameraCapabilities.add(CameraCapability(camSelector, it))
                            }
                        }
                    } catch (exc: java.lang.Exception) {
                        Log.e(TAG, "Camera Face $camSelector is not supported")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 设置屏幕方向固定垂直
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        // 初始化UI
        initializeUI()

        // 绑定录制视频用户实例
        lifecycleScope.launch {
            if (enumerationDeferred != null) {
                enumerationDeferred!!.await()
                enumerationDeferred = null
            }
            initializeQualitySectionsUI()
            bindCaptureUseCase()
        }

        val tipView = TipView(this)
        tipView.isClickable = true // 防止点击事件被透传到 content_view
        (window.decorView as ViewGroup).addView(tipView)

    }

    private fun initializeUI() {

        binding.cameraButton.apply {
            setOnClickListener {
                cameraIndex = (cameraIndex + 1) % cameraCapabilities.size
                qualityIndex = 0
                enableUI(false)
                initializeQualitySectionsUI()
                lifecycleScope.launch {
                    bindCaptureUseCase()
                }
            }
            isEnabled = false
        }

        binding.captureButton.apply {
            setOnClickListener {
                if (!this@CameraActivity::recordingState.isInitialized || recordingState is VideoRecordEvent.Finalize) {
                    enableUI(false)
                    startRecording()
                } else {
                    when (recordingState) {
                        is VideoRecordEvent.Start -> {
                            currentRecording?.pause()
                            binding.stopButton.visibility = View.VISIBLE
                        }
                        is VideoRecordEvent.Pause -> currentRecording?.resume()
                        is VideoRecordEvent.Resume -> currentRecording?.pause()
                        else -> throw IllegalStateException("recordingState in unknown state")
                    }
                }
            }
            isEnabled = false
        }

        binding.stopButton.apply {
            setOnClickListener {
                binding.stopButton.visibility = View.INVISIBLE
                if (currentRecording == null || recordingState is VideoRecordEvent.Finalize) {
                    return@setOnClickListener
                }
                val recording = currentRecording
                if (recording != null) {
                    recording.stop()
                    currentRecording = null
                }
                binding.captureButton.setImageResource(R.drawable.ic_start)
            }
            // ensure the stop button is initialized disabled & invisible
            visibility = View.INVISIBLE
            isEnabled = false
        }



        captureLiveStatus.observe(this) {
            binding.captureStatus.apply {
                post { text = it }
            }
        }
        captureLiveStatus.value = "不录制视频"
    }


    /** 初始化视频质量切换效果*/
    private fun initializeQualitySectionsUI() {
//        binding.tabQuality.removeAllViews()
//        val selectorStrings = cameraCapabilities[cameraIndex].qualities.map { it.getNameString() }
//        selectorStrings.forEach {
//            val textView = TextView(this).apply {
//                gravity = Gravity.CENTER
//                text = it
//            }
//            binding.tabQuality.addView(textView)
//        }
//
//        binding.tabQuality.configTabLayoutConfig {
//            //选中index的回调
//            onSelectIndexChange = { _, selectIndexList, reselect, fromUser ->
//                qualityIndex = selectIndexList.first()
//                enableUI(false)
//                lifecycleScope.launch { bindCaptureUseCase() }
//            }
//        }


        binding.tabCustom.configTabLayoutConfig {
            //选中index的回调
            onSelectIndexChange = { _, selectIndexList, reselect, fromUser ->
                customRatio = selectIndexList.first()
                enableUI(false)
                lifecycleScope.launch { bindCaptureUseCase() }
            }
        }
    }

    private fun startRecording() {
        // create MediaStoreOutputOptions for our recorder: resulting our recording!
        val name = "CameraX-recording-" + SimpleDateFormat(
            FILENAME_FORMAT, Locale.CHINA
        ).format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        // configure Recorder and Start recording to the mediaStoreOutput.
        currentRecording =
            videoCapture.output.prepareRecording(this, mediaStoreOutput).start(mainThreadExecutor, captureListener)

        Log.i(TAG, "Recording started")
    }

    /**
     * CaptureEvent listener.
     */
    private val captureListener = Consumer<VideoRecordEvent> { event ->
        // cache the recording state
        if (event !is VideoRecordEvent.Status) recordingState = event
        updateUI(event)
        if (event is VideoRecordEvent.Finalize) {
            // display the captured video
            Toast.makeText(this, "ad", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 这里需要总是打开 preview + video capture
     * 此函数需要总是执行在主线程上
     */
    private suspend fun bindCaptureUseCase() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()
        val cameraSelector = getCameraSelector(cameraIndex)


        val quality = cameraCapabilities[cameraIndex].qualities[qualityIndex]
        val qualitySelector = QualitySelector.from(quality)

        binding.previewView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            when (customRatio) {
                0 -> dimensionRatio = "V,${ScreenUtils.getAppScreenWidth()}:${ScreenUtils.getAppScreenHeight()}"
                1 -> dimensionRatio = "H,16:9"
                2 -> dimensionRatio = "H,4:3"
            }

        }

        val preview = Preview.Builder().setTargetAspectRatio(quality.getAspectRatio(quality)).build()
            .apply { setSurfaceProvider(binding.previewView.surfaceProvider) }

        val recorder = Recorder.Builder().setQualitySelector(qualitySelector).build()
        videoCapture = VideoCapture.withOutput(recorder)



        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector!!, videoCapture, preview)
        } catch (exc: Exception) {
            // 现在是在主线程，所以需要重置一下控件.
            Log.e(TAG, "Use case binding failed", exc)
            resetUIAndState("bindToLifecycle failed: $exc")
        }
        enableUI(true)
    }


    /**
     * 获取摄像头 CameraSelector
     * @param index Int  CameraSelector.LENS_FACING_BACK 或者 CameraSelector.LENS_FACING_FRONT
     * @return CameraSelector
     */
    private fun getCameraSelector(index: Int): CameraSelector? {
        if (cameraCapabilities.size == 0) {
            Log.i(TAG, "Error: 设备没有摄像头")
            finish()
            return null
        }
        return (cameraCapabilities[index % cameraCapabilities.size].camSelector)
    }


    /**
     * 重置UI（重新打开）
     * 如果 binding 失败，需要重新刷新UI
     * @param reason String
     */
    private fun resetUIAndState(reason: String) {
        enableUI(true)
        showUI(UIState.IDLE, reason)

        cameraIndex = 0
        qualityIndex = 0
        initializeQualitySectionsUI()
    }


    /**
     * 主要用来设置界面上控件的显示
     * @param enable Boolean
     */
    private fun enableUI(enable: Boolean) {
        arrayOf(
            binding.cameraButton, binding.captureButton, binding.stopButton
        ).forEach {
            it.isEnabled = enable
        }
        // disable the camera button if no device to switch
        if (cameraCapabilities.size <= 1) binding.cameraButton.isEnabled = false
    }


    /**
     * 根据不同的状态初始化UI
     * @param state UIState   状态的种类
     * @param status String   状态的名称
     */
    private fun showUI(state: UIState, status: String = "idle") {
        binding.let {
            when (state) {
                UIState.IDLE -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE

                    it.cameraButton.visibility = View.VISIBLE
                }
                UIState.RECORDING -> {
                    it.cameraButton.visibility = View.INVISIBLE

                    it.captureButton.setImageResource(R.drawable.ic_pause)
                    it.captureButton.isEnabled = true
                    it.stopButton.visibility = View.VISIBLE
                    it.stopButton.isEnabled = true
                }
                UIState.FINALIZED -> {
                    it.captureButton.setImageResource(R.drawable.ic_start)
                    it.stopButton.visibility = View.INVISIBLE
                }
                else -> {
                    val errorMsg = "Error: showUI($state) is not supported"
                    Log.e(TAG, errorMsg)
                    return
                }
            }
            it.captureStatus.text = status
        }
    }

    /**
     * 更新UI
     * @param event VideoRecordEvent
     */
    private fun updateUI(event: VideoRecordEvent) {
        val state = if (event is VideoRecordEvent.Status) recordingState.getNameString() else event.getNameString()
        when (event) {
            is VideoRecordEvent.Status -> {
                // placeholder: we update the UI with new status after this when() block,
                // nothing needs to do here.
            }
            is VideoRecordEvent.Start -> showUI(UIState.RECORDING, event.getNameString())
            is VideoRecordEvent.Finalize -> showUI(UIState.FINALIZED, event.getNameString())
            is VideoRecordEvent.Pause -> binding.captureButton.setImageResource(R.drawable.ic_resume)
            is VideoRecordEvent.Resume -> binding.captureButton.setImageResource(R.drawable.ic_pause)
        }
    }


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}