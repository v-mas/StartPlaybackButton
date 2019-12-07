package com.github.vmas.startplaybackbutton

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    private val storage by lazy { AppStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, FloatingViewService::class.java)
        start.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
        stop.setOnClickListener {
            stopService(serviceIntent)
        }
        option_boot_start.isChecked = storage.startAtBoot
        option_boot_start.setOnCheckedChangeListener { _, isChecked ->
            storage.startAtBoot = isChecked
        }
    }
}
