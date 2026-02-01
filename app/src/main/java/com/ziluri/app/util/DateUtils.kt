package com.ziluri.app.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    private val calendar = Calendar.getInstance()
    
    // 获取某天的0点时间戳
    fun getDayStart(timestamp: Long): Long {
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    // 获取今天的0点时间戳
    fun getTodayStart(): Long = getDayStart(System.currentTimeMillis())
    
    // 获取某天的23:59:59时间戳
    fun getDayEnd(timestamp: Long): Long {
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    // 获取某月第一天的0点
    fun getMonthStart(year: Int, month: Int): Long {
        calendar.set(year, month - 1, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    // 获取某月最后一天的23:59:59
    fun getMonthEnd(year: Int, month: Int): Long {
        calendar.set(year, month - 1, 1)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
    
    // 获取某周的第一天（周日）
    fun getWeekStart(timestamp: Long): Long {
        calendar.timeInMillis = timestamp
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return getDayStart(calendar.timeInMillis)
    }
    
    // 获取某周的最后一天（周六）
    fun getWeekEnd(timestamp: Long): Long {
        calendar.timeInMillis = timestamp
        calendar.firstDayOfWeek = Calendar.SUNDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        return getDayEnd(calendar.timeInMillis)
    }
    
    // 判断是否是今天
    fun isToday(timestamp: Long): Boolean {
        val today = getTodayStart()
        val target = getDayStart(timestamp)
        return today == target
    }
    
    // 判断是否是周末
    fun isWeekend(timestamp: Long): Boolean {
        calendar.timeInMillis = timestamp
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }
    
    // 判断两个时间戳是否是同一天
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        return getDayStart(timestamp1) == getDayStart(timestamp2)
    }
    
    // 获取指定月份的天数
    fun getDaysInMonth(year: Int, month: Int): Int {
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
    
    // 获取某天是星期几 (1=周日, 7=周六)
    fun getDayOfWeek(timestamp: Long): Int {
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.DAY_OF_WEEK)
    }
    
    // 获取某天是几号
    fun getDayOfMonth(timestamp: Long): Int {
        calendar.timeInMillis = timestamp
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
    
    // 获取年月
    fun getYearMonth(timestamp: Long): Pair<Int, Int> {
        calendar.timeInMillis = timestamp
        return Pair(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }
    
    // 获取某月第一天是周几 (0=周日, 6=周六)
    fun getFirstDayOfWeekInMonth(year: Int, month: Int): Int {
        calendar.set(year, month - 1, 1)
        return calendar.get(Calendar.DAY_OF_WEEK) - 1
    }
    
    // 添加天数
    fun addDays(timestamp: Long, days: Int): Long {
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.timeInMillis
    }
    
    // 添加月数
    fun addMonths(timestamp: Long, months: Int): Long {
        calendar.timeInMillis = timestamp
        calendar.add(Calendar.MONTH, months)
        return calendar.timeInMillis
    }
    
    // 格式化日期
    fun formatDate(timestamp: Long, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.CHINA)
        return sdf.format(Date(timestamp))
    }
    
    // 格式化为 yyyy年MM月
    fun formatYearMonth(year: Int, month: Int): String {
        return "${year}年${month}月"
    }
    
    // 格式化为 MM月dd日
    fun formatMonthDay(timestamp: Long): String {
        return formatDate(timestamp, "MM月dd日")
    }
    
    // 格式化为完整日期
    fun formatFullDate(timestamp: Long): String {
        return formatDate(timestamp, "yyyy年MM月dd日")
    }
    
    // 获取星期几的中文名
    fun getWeekDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "日"
            Calendar.MONDAY -> "一"
            Calendar.TUESDAY -> "二"
            Calendar.WEDNESDAY -> "三"
            Calendar.THURSDAY -> "四"
            Calendar.FRIDAY -> "五"
            Calendar.SATURDAY -> "六"
            else -> ""
        }
    }
    
    // 计算两个日期之间的天数差
    fun daysBetween(start: Long, end: Long): Int {
        val startDay = getDayStart(start)
        val endDay = getDayStart(end)
        return ((endDay - startDay) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    // 判断是否是连续的天
    fun isConsecutiveDay(day1: Long, day2: Long): Boolean {
        return Math.abs(daysBetween(day1, day2)) == 1
    }
}
