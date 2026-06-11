package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :passwordHash LIMIT 1")
    suspend fun login(username: String, passwordHash: String): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun followUser(follow: Follow)

    @Query("DELETE FROM follows WHERE followerId = :followerId AND followedId = :followedId")
    suspend fun unfollowUser(followerId: Int, followedId: Int)

    @Query("SELECT followedId FROM follows WHERE followerId = :followerId")
    fun getFollowedIds(followerId: Int): Flow<List<Int>>
}
