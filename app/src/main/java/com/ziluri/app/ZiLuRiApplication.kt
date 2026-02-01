package com.ziluri.app

import android.app.Application
import com.ziluri.app.data.database.AppDatabase
import com.ziluri.app.data.repository.AppRepository
import com.ziluri.app.util.PreferencesManager
import com.ziluri.app.util.SoundManager

class ZiLuRiApplication : Application() {
    
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: AppRepository by lazy { AppRepository(database) }
    val preferencesManager: PreferencesManager by lazy { PreferencesManager(this) }
    val soundManager: SoundManager by lazy { SoundManager(this) }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // 应用保存的主题设置
        preferencesManager.applyTheme()
    }
    
    companion object {
        lateinit var instance: ZiLuRiApplication
            private set
    }
}
