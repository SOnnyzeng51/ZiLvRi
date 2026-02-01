package com.ziluri.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.ziluri.app.data.database.Converters

@Entity(tableName = "memos")
@TypeConverters(Converters::class)
data class Memo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val color: String = "#FFFFFF",
    val isPinned: Boolean = false,
    val images: List<String> = emptyList(), // 图片路径列表
    val checkItems: List<MemoCheckItem> = emptyList(), // 待办项列表
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class MemoCheckItem(
    val id: String,
    val content: String,
    val isChecked: Boolean = false
)
