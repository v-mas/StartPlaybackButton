package com.github.vmas.startplaybackbutton

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_CODE_OVERLAY_PERMISION = 287348

class MainActivity : Activity() {

    private val storage by lazy { AppStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    startActivityForResult(
                        Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:$packageName")
                        ),
                        REQUEST_CODE_OVERLAY_PERMISION
                    )
                } else {
                    doStartButtonService()
                }
            } else {
                doStartButtonService()
            }
        }
        stop.setOnClickListener {
            sendBroadcast(Intent(it.context, StopServiceReceiver::class.java))
        }
        option_boot_start.isChecked = storage.startAtBoot
        option_boot_start.setOnCheckedChangeListener { _, isChecked ->
            storage.startAtBoot = isChecked
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAY_PERMISION -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        doStartButtonService()
                    } else {
                        AlertDialog.Builder(this)
                            .setMessage("You must allow this app to display over other apps in order for it to work")
                            .setCancelable(true)
                            .setPositiveButton(android.R.string.ok, null)
                            .create()
                            .show()
                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    private fun doStartButtonService() {
        val serviceIntent = Intent(this, FloatingViewService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

}
