package com.example.smsyard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val onMarkPaid: (Message) -> Unit) :
    ListAdapter<Message, MessageAdapter.MessageViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)
    }

    fun markPaid(id: Long) {
        val idx = currentList.indexOfFirst { it.id == id }
        if (idx != -1) {
            val msg = currentList[idx]
            msg.isPaid = true
            notifyItemChanged(idx)
        }
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bodyText: TextView = itemView.findViewById(R.id.textBody)
        private val button: ImageButton = itemView.findViewById(R.id.buttonMark)

        fun bind(message: Message) {
            bodyText.text = message.body
            if (message.isPaid) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green_paid))
                button.visibility = View.INVISIBLE
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.transparent))
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    onMarkPaid(message)
                }
            }
        }
    }
}
