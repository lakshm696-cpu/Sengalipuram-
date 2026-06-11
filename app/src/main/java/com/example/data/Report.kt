package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class Report(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val username: String,
    val title: String,
    val description: String,
    val location: String,
    val mediaUri: String?,
    val isVideo: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
