package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_updates")
data class ProgressUpdate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reportId: Int,
    val username: String,
    val text: String,
    val mediaUri: String?,
    val isVideo: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
