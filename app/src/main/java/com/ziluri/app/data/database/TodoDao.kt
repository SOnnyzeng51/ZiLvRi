package com.ziluri.app.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ziluri.app.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    // TodoGroup 操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: TodoGroup): Long
    
    @Update
    suspend fun updateGroup(group: TodoGroup)
    
    @Delete
    suspend fun deleteGroup(group: TodoGroup)
    
    @Query("SELECT * FROM todo_groups ORDER BY `order` ASC")
    fun getAllGroups(): Flow<List<TodoGroup>>
    
    @Query("SELECT * FROM todo_groups WHERE id = :id")
    suspend fun getGroupById(id: Long): TodoGroup?
    
    @Query("SELECT MAX(`order`) FROM todo_groups")
    suspend fun getMaxGroupOrder(): Int?
    
    // TodoItem 操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: TodoItem): Long
    
    @Update
    suspend fun updateItem(item: TodoItem)
    
    @Delete
    suspend fun deleteItem(item: TodoItem)
    
    @Query("SELECT * FROM todo_items WHERE id = :id")
    suspend fun getItemById(id: Long): TodoItem?
    
    @Query("SELECT * FROM todo_items WHERE groupId = :groupId ORDER BY `order` ASC")
    fun getItemsByGroup(groupId: Long): Flow<List<TodoItem>>
    
    @Query("SELECT * FROM todo_items WHERE date = :date ORDER BY groupId, `order` ASC")
    fun getItemsByDate(date: Long): Flow<List<TodoItem>>
    
    @Query("""
        SELECT * FROM todo_items 
        WHERE date = :date OR (startDate <= :date AND endDate >= :date)
        ORDER BY groupId, `order` ASC
    """)
    fun getItemsForDate(date: Long): Flow<List<TodoItem>>
    
    @Query("SELECT MAX(`order`) FROM todo_items WHERE groupId = :groupId")
    suspend fun getMaxItemOrder(groupId: Long): Int?
    
    // 获取某天的待办统计
    @Query("""
        SELECT 
            date,
            COUNT(*) as totalCount,
            SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completedCount,
            SUM(CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END) as inProgressCount
        FROM todo_items 
        WHERE date = :date
        GROUP BY date
    """)
    suspend fun getDayTodoSummary(date: Long): DayTodoSummary?
    
    // 获取日期范围内的待办统计
    @Query("""
        SELECT 
            date,
            COUNT(*) as totalCount,
            SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completedCount,
            SUM(CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END) as inProgressCount
        FROM todo_items 
        WHERE date >= :startDate AND date <= :endDate
        GROUP BY date
    """)
    fun getDateRangeTodoSummary(startDate: Long, endDate: Long): Flow<List<DayTodoSummary>>
    
    // 获取组及其所有待办项
    @Transaction
    @Query("SELECT * FROM todo_groups ORDER BY `order` ASC")
    fun getGroupsWithItems(): Flow<List<TodoGroupWithItems>>
    
    // 获取某天的组及待办项
    @Transaction
    @Query("""
        SELECT DISTINCT g.* FROM todo_groups g
        INNER JOIN todo_items i ON g.id = i.groupId
        WHERE i.date = :date OR (i.startDate <= :date AND i.endDate >= :date)
        ORDER BY g.`order` ASC
    """)
    fun getGroupsWithItemsForDate(date: Long): Flow<List<TodoGroupWithItems>>
    
    // 检查某天是否所有任务都完成
    @Query("""
        SELECT CASE 
            WHEN COUNT(*) = 0 THEN 0
            WHEN SUM(CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END) = 0 THEN 1
            ELSE 0
        END
        FROM todo_items 
        WHERE date = :date
    """)
    suspend fun isAllCompletedForDate(date: Long): Boolean
    
    // 统计完成的任务数
    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    fun getTotalCompletedCount(): Flow<Int>
}
