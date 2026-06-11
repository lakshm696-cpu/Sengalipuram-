package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Report::class, User::class, Follow::class, Message::class, ProgressUpdate::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reportDao(): ReportDao
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun progressUpdateDao(): ProgressUpdateDao
}
