package com.gredicer.basestudy

import android.content.Intent
import android.os.Bundle
import com.gredicer.basestudy.base.BaseBindingActivity
import com.gredicer.basestudy.databinding.ActivityMainBinding
import com.gredicer.basestudy.util.DarkModeUtils
import com.gredicer.basestudy.util.LanguageUtils
import java.util.*


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding.switchTheme.setOnClickListener {
            DarkModeUtils.setDarkMode(!DarkModeUtils.isDarkMode(this))
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.layout_in, R.anim.layout_out)
            finish()
        }


        binding.switchLanguage.setOnClickListener {
            val isEnglish = LanguageUtils.getLanguage(this) == Locale.ENGLISH
            val language = if (!isEnglish) Locale.ENGLISH else Locale.CHINESE
            LanguageUtils.setLanguage(this, language)
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.layout_in, R.anim.layout_out)
            finish()
        }
    }

}