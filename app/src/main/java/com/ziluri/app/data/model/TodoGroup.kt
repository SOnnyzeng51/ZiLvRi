package com.ziluri.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_groups")
data class TodoGroup(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
