package com.ziluri.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class Priority(val value: Int) {
    LOW(0),
    MEDIUM(1),
    HIGH(2),
    URGENT(3)
}

@Entity(
    tableName = "todo_items",
    foreignKeys = [
        ForeignKey(
            entity = TodoGroup::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("date")]
)
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val content: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM,
    val date: Long, // 日期时间戳（只保留日期部分）
    val startDate: Long? = null, // 多日任务开始日期
    val endDate: Long? = null, // 多日任务结束日期
    val requiredCompletions: Int = 1, // 需要完成的次数
    val currentCompletions: Int = 0, // 当前完成次数
    val order: Int = 0,
    val completedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isMultiDay: Boolean
        get() = startDate != null && endDate != null && startDate != endDate
    
    val isFullyCompleted: Boolean
        get() = currentCompletions >= requiredCompletions
}
