package com.gredicer.utilstudy

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.datetime.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentMoment = Clock.System.now()      // 2022-01-21T06:46:33.052Z
        Log.e(TAG, "onCreate: $currentMoment")
        val datetimeInUtc: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
        Log.e(TAG, "onCreate: $datetimeInUtc")
        val datetimeInSystemZone: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.currentSystemDefault())
        Log.e(TAG, "onCreate: $datetimeInSystemZone")
        val kotlinReleaseDateTime = LocalDateTime(2016, 2, 15, 16, 57, 0, 0)
        Log.e(TAG, "onCreate: $kotlinReleaseDateTime")
        val knownDate = LocalDate(2020, 2, 21)
        Log.e(TAG, "onCreate: $knownDate")


    }
}