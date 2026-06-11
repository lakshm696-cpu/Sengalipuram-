package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressUpdateDao {
    @Query("SELECT * FROM progress_updates WHERE reportId = :reportId ORDER BY timestamp DESC")
    fun getUpdatesForReport(reportId: Int): Flow<List<ProgressUpdate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: ProgressUpdate)

    @Query("DELETE FROM progress_updates WHERE id = :id")
    suspend fun deleteUpdateById(id: Int)
}
