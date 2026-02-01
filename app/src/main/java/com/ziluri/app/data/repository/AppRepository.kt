package com.ziluri.app.data.repository

import com.ziluri.app.data.database.AppDatabase
import com.ziluri.app.data.model.*
import kotlinx.coroutines.flow.Flow

class AppRepository(private val database: AppDatabase) {
    
    private val todoDao = database.todoDao()
    private val memoDao = database.memoDao()
    private val userDao = database.userDao()
    
    // ========== TodoGroup ==========
    
    fun getAllGroups(): Flow<List<TodoGroup>> = todoDao.getAllGroups()
    
    suspend fun insertGroup(group: TodoGroup): Long {
        val maxOrder = todoDao.getMaxGroupOrder() ?: -1
        return todoDao.insertGroup(group.copy(order = maxOrder + 1))
    }
    
    suspend fun updateGroup(group: TodoGroup) = todoDao.updateGroup(group.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun deleteGroup(group: TodoGroup) = todoDao.deleteGroup(group)
    
    suspend fun getGroupById(id: Long): TodoGroup? = todoDao.getGroupById(id)
    
    // ========== TodoItem ==========
    
    fun getItemsByDate(date: Long): Flow<List<TodoItem>> = todoDao.getItemsByDate(date)
    
    fun getItemsForDate(date: Long): Flow<List<TodoItem>> = todoDao.getItemsForDate(date)
    
    suspend fun insertItem(item: TodoItem): Long {
        val maxOrder = todoDao.getMaxItemOrder(item.groupId) ?: -1
        return todoDao.insertItem(item.copy(order = maxOrder + 1))
    }
    
    suspend fun updateItem(item: TodoItem) = todoDao.updateItem(item.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun deleteItem(item: TodoItem) = todoDao.deleteItem(item)
    
    suspend fun getItemById(id: Long): TodoItem? = todoDao.getItemById(id)
    
    suspend fun completeItem(item: TodoItem): Boolean {
        val newCompletions = item.currentCompletions + 1
        val isFullyCompleted = newCompletions >= item.requiredCompletions
        
        todoDao.updateItem(item.copy(
            currentCompletions = newCompletions,
            isCompleted = isFullyCompleted,
            completedAt = if (isFullyCompleted) System.currentTimeMillis() else null,
            updatedAt = System.currentTimeMillis()
        ))
        
        return isFullyCompleted
    }
    
    suspend fun uncompleteItem(item: TodoItem) {
        todoDao.updateItem(item.copy(
            currentCompletions = (item.currentCompletions - 1).coerceAtLeast(0),
            isCompleted = false,
            completedAt = null,
            updatedAt = System.currentTimeMillis()
        ))
    }
    
    fun getGroupsWithItemsForDate(date: Long): Flow<List<TodoGroupWithItems>> = 
        todoDao.getGroupsWithItemsForDate(date)
    
    fun getDateRangeTodoSummary(startDate: Long, endDate: Long): Flow<List<DayTodoSummary>> =
        todoDao.getDateRangeTodoSummary(startDate, endDate)
    
    suspend fun isAllCompletedForDate(date: Long): Boolean = todoDao.isAllCompletedForDate(date)
    
    fun getTotalCompletedCount(): Flow<Int> = todoDao.getTotalCompletedCount()
    
    // ========== Memo ==========
    
    fun getAllMemos(): Flow<List<Memo>> = memoDao.getAllMemos()
    
    suspend fun insertMemo(memo: Memo): Long = memoDao.insert(memo)
    
    suspend fun updateMemo(memo: Memo) = memoDao.update(memo.copy(updatedAt = System.currentTimeMillis()))
    
    suspend fun deleteMemo(memo: Memo) = memoDao.delete(memo)
    
    suspend fun getMemoById(id: Long): Memo? = memoDao.getMemoById(id)
    
    fun searchMemos(query: String): Flow<List<Memo>> = memoDao.searchMemos(query)
    
    // ========== User ==========
    
    fun getCurrentUser(): Flow<User?> = userDao.getCurrentUser()
    
    suspend fun getCurrentUserSync(): User? = userDao.getCurrentUserSync()
    
    suspend fun insertUser(user: User) = userDao.insert(user)
    
    suspend fun updateUser(user: User) = userDao.update(user)
    
    suspend fun deleteUser(user: User) = userDao.delete(user)
    
    suspend fun deleteAllUsers() = userDao.deleteAll()
    
    suspend fun addExpAndCheckLevelUp(userId: String, amount: Int): Boolean {
        userDao.addExp(userId, amount)
        val user = userDao.getUserById(userId) ?: return false
        
        if (user.canLevelUp()) {
            userDao.levelUp(userId)
            return true
        }
        return false
    }
    
    suspend fun incrementCompleted(userId: String) = userDao.incrementCompleted(userId)
    
    suspend fun updateStreak(userId: String, days: Int, date: Long) = userDao.updateStreak(userId, days, date)
    
    // 创建游客用户
    suspend fun createGuestUser(): User {
        val user = User(
            id = "guest_${System.currentTimeMillis()}",
            nickname = "游客",
            loginType = LoginType.GUEST
        )
        userDao.insert(user)
        return user
    }
}
