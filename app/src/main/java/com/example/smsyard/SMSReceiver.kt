package com.example.smsyard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val bundle = intent.extras ?: return
        val pdus = bundle.get("pdus") as? Array<*>
        val msgs = mutableListOf<SmsMessage>()
        if (pdus != null) {
            for (pdu in pdus) {
                val format = bundle.getString("format")
                msgs.add(SmsMessage.createFromPdu(pdu as ByteArray, format))
            }
            // Could trigger UI update via LiveData or similar, kept minimal here
        }
    }
}
