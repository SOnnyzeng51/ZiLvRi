package com.ziluri.app.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TodoGroupWithItems(
    @Embedded val group: TodoGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val items: List<TodoItem>
)

data class DayTodoSummary(
    val date: Long,
    val totalCount: Int,
    val completedCount: Int,
    val inProgressCount: Int
) {
    val isAllCompleted: Boolean
        get() = totalCount > 0 && completedCount == totalCount
    
    val hasInProgress: Boolean
        get() = inProgressCount > 0
}
