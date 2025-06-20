package com.example.smsyard

data class Message(
    val id: Long,
    val body: String,
    val date: Long,
    var isPaid: Boolean = false
)
