package com.github.vmas.startplaybackbutton

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.*

inline fun Activity.LinearLayout(configuration: LinearLayout.() -> Unit) =
    LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        configuration()
    }

inline fun ViewGroup.button(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    configuration: Button.() -> Unit
): Button = Button(context).apply(configuration).also { this.addView(it, width, height) }

inline fun ViewGroup.checkbox(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    configuration: CheckBox.() -> Unit
): CheckBox = CheckBox(context).apply(configuration).also { this.addView(it, width, height) }

inline fun ViewGroup.label(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    configuration: TextView.() -> Unit
) = TextView(context).apply(configuration).also { addView(it, width, height) }

inline fun ViewGroup.selector(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    configuration: Spinner.() -> Unit
) = Spinner(context).apply(configuration).also { addView(it, width, height) }

inline var TextView.drawableStart: Drawable?
    get() = compoundDrawablesRelative[0]
    set(value) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(
            value,
            compoundDrawablesRelative[1],
            compoundDrawablesRelative[2],
            compoundDrawablesRelative[3]
        )
    }

inline fun <T> Spinner.adapter(
    items: List<T>,
    crossinline viewCreator: (item: T, convertView: View?, parent: ViewGroup) -> View
) {
    adapter = object : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
            viewCreator(items[position], convertView, parent)

        override fun getItem(position: Int): T = items[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getCount(): Int = items.size
    }
}
