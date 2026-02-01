package com.ziluri.app.data.model

data class CalendarDay(
    val date: Long, // 日期时间戳
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isWeekend: Boolean = false,
    val hasTodos: Boolean = false,
    val hasInProgressTodos: Boolean = false, // 有进行中的待办
    val allCompleted: Boolean = false // 所有待办都已完成
)

data class CalendarMonth(
    val year: Int,
    val month: Int, // 1-12
    val days: List<CalendarDay>
)

data class CalendarWeek(
    val weekOfYear: Int,
    val days: List<CalendarDay>
)

enum class CalendarViewType {
    YEAR,
    MONTH,
    WEEK,
    DAY
}
