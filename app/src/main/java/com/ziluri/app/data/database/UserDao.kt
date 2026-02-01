package com.ziluri.app.data.database

import androidx.room.*
import com.ziluri.app.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)
    
    @Update
    suspend fun update(user: User)
    
    @Delete
    suspend fun delete(user: User)
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?
    
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserByIdFlow(id: String): Flow<User?>
    
    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<User?>
    
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUserSync(): User?
    
    @Query("DELETE FROM users")
    suspend fun deleteAll()
    
    @Query("UPDATE users SET exp = exp + :amount WHERE id = :userId")
    suspend fun addExp(userId: String, amount: Int)
    
    @Query("UPDATE users SET level = level + 1 WHERE id = :userId")
    suspend fun levelUp(userId: String)
    
    @Query("UPDATE users SET totalCompleted = totalCompleted + 1 WHERE id = :userId")
    suspend fun incrementCompleted(userId: String)
    
    @Query("UPDATE users SET continuousDays = :days, lastActiveDate = :date WHERE id = :userId")
    suspend fun updateStreak(userId: String, days: Int, date: Long)
}
