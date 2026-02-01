package com.ziluri.app.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.ziluri.app.R

class SoundManager(private val context: Context) {
    
    private val soundPool: SoundPool
    private var completeSound: Int = 0
    private var achievementSound: Int = 0
    private var clickSound: Int = 0
    
    private val vibrator: Vibrator
    private val prefsManager: PreferencesManager
    
    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // 加载音效 - 这里使用占位符，实际需要添加音效文件
        // completeSound = soundPool.load(context, R.raw.complete, 1)
        // achievementSound = soundPool.load(context, R.raw.achievement, 1)
        // clickSound = soundPool.load(context, R.raw.click, 1)
        
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        prefsManager = PreferencesManager(context)
    }
    
    // 播放完成任务音效（清脆的勾选声）
    fun playCompleteSound() {
        if (prefsManager.soundEnabled && completeSound != 0) {
            soundPool.play(completeSound, 1f, 1f, 1, 0, 1f)
        }
        vibrateShort()
    }
    
    // 播放成就音效（史诗成就音效）
    fun playAchievementSound() {
        if (prefsManager.soundEnabled && achievementSound != 0) {
            soundPool.play(achievementSound, 1f, 1f, 1, 0, 1f)
        }
        vibrateLong()
    }
    
    // 播放点击音效
    fun playClickSound() {
        if (prefsManager.soundEnabled && clickSound != 0) {
            soundPool.play(clickSound, 0.5f, 0.5f, 1, 0, 1f)
        }
        vibrateTiny()
    }
    
    // 短震动（完成任务）
    private fun vibrateShort() {
        if (!prefsManager.vibrateEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(30)
        }
    }
    
    // 长震动（成就）
    private fun vibrateLong() {
        if (!prefsManager.vibrateEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 100, 50, 100, 50, 200)
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 100, 50, 100, 50, 200), -1)
        }
    }
    
    // 轻微震动（点击）
    private fun vibrateTiny() {
        if (!prefsManager.vibrateEnabled) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(10)
        }
    }
    
    fun release() {
        soundPool.release()
    }
}
