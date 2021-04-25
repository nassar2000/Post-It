package com.mready.postit.helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.mready.postit.model.User
import com.mready.postit.ui.auth.RegisterActivity


class SharedPrefManager(context: Context){

    private val preferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCE_NAME,
        PRIVATE_MODE)

    companion object {
        private const val PRIVATE_MODE = 0
        private const val PREFERENCE_NAME = "configuration"
        private const val KEY_ID: String = "id"
        private const val KEY_USERNAME: String = "username"
        private const val KEY_DISPLAYNAME: String = "display_name"
        private const val KEY_TOKEN: String = "token"

    }

    fun userLogin(user: User) {
        val editor = preferences.edit()
        editor?.putInt(KEY_ID, user.id ?: return)
        editor?.putString(KEY_USERNAME, user.username)
        editor?.putString(KEY_DISPLAYNAME, user.displayName)
        editor?.putString(KEY_TOKEN, user.token)
        editor?.apply()
    }

    fun logout() {
        val editor = preferences.edit()
        editor?.clear()
        editor?.apply()
    }

    fun isLoggedIn(): Boolean {
        return preferences.getString(KEY_USERNAME, null) != null
    }

    fun getToken(): String? {
        return preferences.getString(KEY_TOKEN, null)
    }


}