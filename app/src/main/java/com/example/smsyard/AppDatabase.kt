package com.example.smsyard

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PaidMessage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun paidDao(): PaidDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "smsyard.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
