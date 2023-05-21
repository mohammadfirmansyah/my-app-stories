package com.dicoding.myappstories.utilities

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.myappstories.utilities.ConstValue.KEY_EMAIL
import com.dicoding.myappstories.utilities.ConstValue.KEY_IS_LOGIN
import com.dicoding.myappstories.utilities.ConstValue.KEY_USER_NAME

class SessionManager(context: Context) {
    private val pref: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "Session"
        private const val PRIVATE_MODE = 0
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    fun saveAuthToken(token: String) {
        val editor = pref.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return pref.getString(KEY_AUTH_TOKEN, null)
    }

    fun clearAuthToken() {
        val editor = pref.edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.apply()
    }

    fun clearPreferences() {
        val editor = pref.edit()
        editor.clear().apply()
    }

    fun setStringPreference(prefKey: String, value: String) {
        val editor = pref.edit()
        editor.putString(prefKey, value)
        editor.apply()
    }

    fun setBooleanPreference(prefKey: String, value: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(prefKey, value)
        editor.apply()
    }

    val getUserName = pref.getString(KEY_USER_NAME, "")
    val getEmail = pref.getString(KEY_EMAIL, "")
    val isLogin = pref.getBoolean(KEY_IS_LOGIN, false)

}