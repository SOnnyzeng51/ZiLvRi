package com.ziluri.app.data.database

import androidx.room.*
import com.ziluri.app.data.model.Memo
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memo: Memo): Long
    
    @Update
    suspend fun update(memo: Memo)
    
    @Delete
    suspend fun delete(memo: Memo)
    
    @Query("SELECT * FROM memos ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllMemos(): Flow<List<Memo>>
    
    @Query("SELECT * FROM memos WHERE id = :id")
    suspend fun getMemoById(id: Long): Memo?
    
    @Query("SELECT * FROM memos WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchMemos(query: String): Flow<List<Memo>>
    
    @Query("SELECT COUNT(*) FROM memos")
    fun getMemoCount(): Flow<Int>
}
