package com.example.data

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    suspend fun login(username: String, passwordHash: String): User? {
        return userDao.login(username, passwordHash)
    }

    suspend fun register(username: String, passwordHash: String): User? {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) return null // Already exists
        val user = User(username = username, passwordHash = passwordHash)
        val id = userDao.insertUser(user)
        return user.copy(id = id.toInt())
    }

    suspend fun followUser(followerId: Int, followedId: Int) {
        userDao.followUser(Follow(followerId, followedId))
    }

    suspend fun unfollowUser(followerId: Int, followedId: Int) {
        userDao.unfollowUser(followerId, followedId)
    }

    fun getFollowedIds(followerId: Int): Flow<List<Int>> {
        return userDao.getFollowedIds(followerId)
    }
}
