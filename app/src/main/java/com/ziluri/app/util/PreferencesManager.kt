package com.ziluri.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "ziluri_prefs"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATE_ENABLED = "vibrate_enabled"
        
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
    }
    
    // 主题模式
    var themeMode: Int
        get() = prefs.getInt(KEY_THEME_MODE, THEME_SYSTEM)
        set(value) {
            prefs.edit().putInt(KEY_THEME_MODE, value).apply()
            applyTheme(value)
        }
    
    // 用户ID
    var userId: String?
        get() = prefs.getString(KEY_USER_ID, null)
        set(value) = prefs.edit().putString(KEY_USER_ID, value).apply()
    
    // 是否首次启动
    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()
    
    // 通知开关
    var notificationEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, value).apply()
    
    // 音效开关
    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()
    
    // 震动开关
    var vibrateEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATE_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATE_ENABLED, value).apply()
    
    // 应用主题
    fun applyTheme(mode: Int = themeMode) {
        val nightMode = when (mode) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
    
    // 清除所有偏好
    fun clear() {
        prefs.edit().clear().apply()
    }
    
    // 退出登录时清除用户相关数据
    fun clearUserData() {
        prefs.edit()
            .remove(KEY_USER_ID)
            .apply()
    }
}
