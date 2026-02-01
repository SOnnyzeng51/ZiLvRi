package com.ziluri.app.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.ziluri.app.ZiLuRiApplication

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Handler(Looper.getMainLooper()).postDelayed({
            val prefs = (application as ZiLuRiApplication).preferencesManager
            
            val intent = if (prefs.isFirstLaunch || prefs.userId == null) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 1500)
    }
}
