package com.example.smsyard

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PaidDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(msg: PaidMessage)

    @Query("SELECT smsId FROM paid_messages")
    suspend fun getAllIds(): List<Long>
}
