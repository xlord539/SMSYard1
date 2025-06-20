package com.example.smsyard

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var db: AppDatabase
    private val SMS_URI_INBOX: Uri = Uri.parse("content://sms/inbox")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MessageAdapter { message ->
            CoroutineScope(Dispatchers.IO).launch {
                db.paidDao().insert(PaidMessage(message.id))
                withContext(Dispatchers.Main) {
                    adapter.markPaid(message.id)
                }
            }
        }
        recyclerView.adapter = adapter

        checkPermissionsAndLoad()
    }

    private fun checkPermissionsAndLoad() {
        val needed = arrayOf(Manifest.permission.READ_SMS)
        val denied = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (denied.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, denied.toTypedArray(), 100)
        } else {
            loadSms()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            loadSms()
        }
    }

    private fun loadSms() {
        CoroutineScope(Dispatchers.IO).launch {
            val paidSet = db.paidDao().getAllIds().toSet()
            val list = mutableListOf<Message>()
            val projection = arrayOf("_id", "address", "body", "date")
            val cursor: Cursor? = contentResolver.query(SMS_URI_INBOX, projection, null, null, "date DESC")
            cursor?.use {
                val idIdx = it.getColumnIndex("_id")
                val bodyIdx = it.getColumnIndex("body")
                val dateIdx = it.getColumnIndex("date")
                while (it.moveToNext()) {
                    val id = it.getLong(idIdx)
                    val body = it.getString(bodyIdx)
                    val date = it.getLong(dateIdx)
                    list.add(Message(id, body, date, paidSet.contains(id)))
                }
            }
            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }
    }
}
