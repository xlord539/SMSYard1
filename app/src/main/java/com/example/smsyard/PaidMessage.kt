package com.example.smsyard

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paid_messages")
data class PaidMessage(
    @PrimaryKey val smsId: Long
)
