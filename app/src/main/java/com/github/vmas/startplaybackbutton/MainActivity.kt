package com.github.vmas.startplaybackbutton

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.AdapterView
import android.widget.TextView

private const val REQUEST_CODE_OVERLAY_PERMISSION = 287348

@SuppressLint("SetTextI18n")
class MainActivity : Activity() {

    private val storage by lazy { AppStorage(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LinearLayout {
            label {
                text = "Choose target media player:"
            }
            selector(height = WRAP_CONTENT) {
                val items = listOf(null) + packageManager.queryBroadcastReceivers(
                    Intent("android.intent.action.MEDIA_BUTTON"),
                    PackageManager.GET_META_DATA
                )
                adapter(items) { item, convertView, parent ->
                    if (convertView is TextView) {
                        convertView
                    } else {
                        TextView(parent.context).apply {
                            gravity = Gravity.START or Gravity.CENTER_VERTICAL
                            minHeight = 36.dp(context)
                            setPaddingRelative(
                                8.dp(context),
                                8.dp(context),
                                8.dp(context),
                                8.dp(context)
                            )
                        }
                    }.apply {
                        if (item == null) {
                            text = "last used"
                            drawableStart = null
                        } else {
                            text =
                                "${item.loadLabel(packageManager)} (${item.activityInfo.packageName})"
                            drawableStart = item.loadIcon(packageManager)
                        }
                    }
                }
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        storage.targetMediaPlayerPackage = null
                    }

                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        storage.targetMediaPlayerPackage =
                            items[position]?.activityInfo?.packageName
                    }
                }
                val previousSelectionIndex =
                    items.indexOfFirst { it?.activityInfo?.packageName == storage.targetMediaPlayerPackage }
                setSelection(if (previousSelectionIndex < 0) 0 else previousSelectionIndex)
            }
            button {
                text = "Show Button"
                setOnClickListener {
                    tryStartService()
                }
            }
            button {
                text = "Hide Button"
                setOnClickListener {
                    doStopService()
                }
            }
            checkbox(width = WRAP_CONTENT) {
                text = "Start at system boot"
                isChecked = storage.startAtBoot
                setOnCheckedChangeListener { _, isChecked ->
                    storage.startAtBoot = isChecked
                }
            }
        })

        if (storage.startAtBoot) {
            tryStartService()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAY_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        doStartService()
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

    private fun tryStartService() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ),
                    REQUEST_CODE_OVERLAY_PERMISSION
                )
            } else {
                doStartService()
            }
        } else {
            doStartService()
        }
    }

    private fun doStartService() {
        val serviceIntent = Intent(this, FloatingViewService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun doStopService() {
        sendBroadcast(Intent(this, StopServiceReceiver::class.java))
    }
}
