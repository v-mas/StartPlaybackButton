package com.github.vmas.startplaybackbutton

import android.content.Context

class AppStorage(context: Context) {
    private val sharedPrefs = context.getSharedPreferences(
        "com.github.v-mas.StartPlaybackButton",
        Context.MODE_PRIVATE
    )
    var startAtBoot: Boolean by sharedPrefs.booleanProperty(def = true)

    var buttonSize: Int by sharedPrefs.intProperty(def = 48)
    var buttonPosX: Int by sharedPrefs.intProperty()
    var buttonPosY: Int by sharedPrefs.intProperty()
}
