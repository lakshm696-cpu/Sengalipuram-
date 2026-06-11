package com.example.data

import androidx.room.Entity

@Entity(
    tableName = "follows",
    primaryKeys = ["followerId", "followedId"]
)
data class Follow(
    val followerId: Int,
    val followedId: Int
)
