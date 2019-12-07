package com.github.vmas.startplaybackbutton

import android.content.Context
import android.util.TypedValue

fun Float.dp(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

fun Int.dp(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
