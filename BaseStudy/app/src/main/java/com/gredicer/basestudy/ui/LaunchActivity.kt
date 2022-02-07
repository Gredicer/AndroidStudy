package com.gredicer.basestudy.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gredicer.basestudy.MainActivity
import com.gredicer.basestudy.base.BaseBindingActivity
import com.gredicer.basestudy.databinding.ActivityLaunchBinding

class LaunchActivity : BaseBindingActivity<ActivityLaunchBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread {
            Thread.sleep(2000);
            //耗时任务，比如加载网络数据
            runOnUiThread { // 这里可以睡几秒钟，如果要放广告的话
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }.start()
    }
}