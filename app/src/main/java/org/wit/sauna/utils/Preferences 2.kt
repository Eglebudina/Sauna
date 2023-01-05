package org.wit.sauna.utils

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    const val PREF_NAME = "PREFERENCES_APP"
    const val MODE = Context.MODE_PRIVATE
    fun writeInt(context: Context, key: String, value: Int): String {
        getEditor(context).putInt(key, value).commit()
        return key
    }

    fun readString(context: Context, key: String?): String? {
        return getPreferences(context).getString(key, "")
    }

    fun writeString(context: Context, key: String, value: String?): String {
        getEditor(context).putString(key, value).apply()
        return key
    }

    fun readInt(context: Context, key: String?, defValue: Int): Int {
        return getPreferences(context).getInt(key, defValue)
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context
            .getSharedPreferences(PREF_NAME, MODE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }
}