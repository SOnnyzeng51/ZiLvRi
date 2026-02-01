package com.ziluri.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LoginType {
    GUEST,
    QQ,
    WECHAT
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val nickname: String = "用户",
    val avatar: String? = null,
    val loginType: LoginType = LoginType.GUEST,
    val level: Int = 1,
    val exp: Int = 0,
    val totalCompleted: Int = 0,
    val continuousDays: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        // 等级经验值配置
        fun getExpForLevel(level: Int): Int {
            return when {
                level <= 5 -> level * 100
                level <= 10 -> 500 + (level - 5) * 200
                level <= 20 -> 1500 + (level - 10) * 300
                level <= 50 -> 4500 + (level - 20) * 500
                else -> 19500 + (level - 50) * 1000
            }
        }
        
        // 获取等级称号
        fun getLevelTitle(level: Int): String {
            return when {
                level < 5 -> "自律新手"
                level < 10 -> "自律学徒"
                level < 20 -> "自律达人"
                level < 35 -> "自律专家"
                level < 50 -> "自律大师"
                level < 75 -> "自律宗师"
                else -> "自律传奇"
            }
        }
        
        // 获取等级颜色
        fun getLevelColorRes(level: Int): Int {
            return when {
                level < 10 -> com.ziluri.app.R.color.level_bronze
                level < 25 -> com.ziluri.app.R.color.level_silver
                level < 50 -> com.ziluri.app.R.color.level_gold
                level < 75 -> com.ziluri.app.R.color.level_platinum
                else -> com.ziluri.app.R.color.level_diamond
            }
        }
    }
    
    // 完成任务获得的经验值
    fun getExpReward(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> 5
            Priority.MEDIUM -> 10
            Priority.HIGH -> 20
            Priority.URGENT -> 30
        }
    }
    
    // 检查是否可以升级
    fun canLevelUp(): Boolean {
        return exp >= getExpForLevel(level)
    }
    
    // 获取下一级需要的经验
    fun getExpToNextLevel(): Int {
        return getExpForLevel(level) - exp
    }
    
    // 获取当前等级进度 (0.0 - 1.0)
    fun getLevelProgress(): Float {
        val required = getExpForLevel(level)
        val prevRequired = if (level > 1) getExpForLevel(level - 1) else 0
        val currentLevelExp = exp - prevRequired
        val neededForThisLevel = required - prevRequired
        return (currentLevelExp.toFloat() / neededForThisLevel).coerceIn(0f, 1f)
    }
}
