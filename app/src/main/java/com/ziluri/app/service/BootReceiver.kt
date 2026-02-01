package com.ziluri.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 设备启动后重新设置提醒
            val serviceIntent = Intent(context, ReminderService::class.java)
            context.startService(serviceIntent)
        }
    }
}
