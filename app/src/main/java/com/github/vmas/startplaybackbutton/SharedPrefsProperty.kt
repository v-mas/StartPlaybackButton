package com.github.vmas.startplaybackbutton

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun <reified T> SharedPreferences.property(
    key: String?,
    def: T,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        getter(key ?: property.name, def)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        edit().setter(key ?: property.name, value).apply()
    }
}

fun SharedPreferences.booleanProperty(key: String? = null, def: Boolean = false) =
    property(key, def, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.intProperty(key: String? = null, def: Int = 0) =
    property(key, def, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.stringProperty(key: String? = null, def: String? = null) =
    property(key, def, SharedPreferences::getString, SharedPreferences.Editor::putString)
