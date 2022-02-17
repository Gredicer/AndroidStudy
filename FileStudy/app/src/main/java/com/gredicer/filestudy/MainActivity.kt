package com.gredicer.filestudy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.lruCache
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {

    private lateinit var requestFileIntent: Intent
    private lateinit var inputPFD: ParcelFileDescriptor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestFileIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/jpg"
        }
//        val file = File(filesDir, "asd")
//
//        val fileName = "qwe"
//        val fileContents = "hello world"
//        openFileOutput(fileName, Context.MODE_PRIVATE).use {
//            it.write(fileContents.toByteArray())
//        }


        val a = findViewById<Button>(R.id.test)
        a.setOnClickListener {
//            resultLauncher.launch(requestFileIntent)
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>?) {}
                    override fun onCancel() {}
                })
        }








    }

}