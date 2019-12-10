package com.github.vmas.startplaybackbutton

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView
import kotlin.math.abs

private const val NOTIFICATION_ID = 89468946

private const val STOP_SERVICE_REQUEST_CODE = 7264

class FloatingViewService : Service() {

    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val storage by lazy { AppStorage(this) }

    private var fab: View? = null

    override fun onCreate() {
        super.onCreate()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.ensureNotificationChannelExists()

        val notification = Notification.Builder(this).apply {
            if (Build.VERSION.SDK_INT >= 26) {
                setChannelId(FOREGROUND_SERVICE_NOTIFICATION_CHANNEL_ID)
            }
            setContentTitle("Playback Start Button is showing")
            setContentText("Click this notification to hide button")
            setOngoing(true)
            setContentIntent(
                PendingIntent.getBroadcast(
                    this@FloatingViewService,
                    STOP_SERVICE_REQUEST_CODE,
                    Intent(this@FloatingViewService, StopServiceReceiver::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            setPriority(Notification.PRIORITY_MIN)
            setSmallIcon(R.mipmap.ic_launcher)
        }
            .build()

        startForeground(NOTIFICATION_ID, notification)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= 26) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            x = storage.buttonPosX
            y = storage.buttonPosY
        }

        fab = ImageView(this).apply {
            background = ShapeDrawable(OvalShape())
            setImageResource(R.drawable.ic_play_arrow)
            minimumWidth = storage.buttonSize.dp(context)
            minimumHeight = storage.buttonSize.dp(context)

            //TODO button style from sharedPrefs
            backgroundTintMode = PorterDuff.Mode.SRC_IN
            backgroundTintList = ColorStateList.valueOf(Color.YELLOW)
            imageTintMode = PorterDuff.Mode.SRC_IN
            imageTintList = ColorStateList.valueOf(Color.BLUE)
            elevation = 8f.dp(context)
            setOnClickListener {
                //context.startActivity(Intent(context, MainActivity::class.java).apply {
                //    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //})
                audioManager.dispatchMediaKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    )
                )
                audioManager.dispatchMediaKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    )
                )
            }
            setOnTouchListener(object : OnTouchListener {
                private val minMoveDistance =
                    ViewConfiguration.get(context).scaledTouchSlop

                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                        }
                        MotionEvent.ACTION_UP -> {
                            if (!isMovedEnough(event)) {
                                v.performClick()
                            } else {
                                storage.buttonPosX = params.x
                                storage.buttonPosY = params.y
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (isMovedEnough(event)) {
                                params.x = initialX + (event.rawX - initialTouchX).toInt()
                                params.y = initialY + (event.rawY - initialTouchY).toInt()
                                windowManager.updateViewLayout(v, params)
                            }
                        }
                    }
                    return true
                }

                private fun isMovedEnough(event: MotionEvent): Boolean {
                    return abs(event.rawX - initialTouchX) > minMoveDistance ||
                            abs(event.rawY - initialTouchY) > minMoveDistance
                }
            })
        }

        windowManager.addView(fab, params)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        fab?.also {
            fab = null
            windowManager.removeView(it)
        }
        super.onDestroy()
    }

    override fun onBind(startIntent: Intent): IBinder? {
        return null
    }
}
