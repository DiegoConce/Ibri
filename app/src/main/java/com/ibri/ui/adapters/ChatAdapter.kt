package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemChatMessageBinding
import com.ibri.model.messaging.Message
import com.ibri.utils.GET_MEDIA_ENDPOINT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val context: Context) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val messages = ArrayList<Message>()

    fun setData(list: List<Message>) {
        messages.clear()
        messages.addAll(list)
        notifyItemRangeChanged(0, messages.size)
    }

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = messages[position]

        if (item.privateSender != null) {
            holder.binding.chatSender.text =
                "${item.privateSender?.name} ${item.privateSender?.surname}"
            holder.binding.chatOwner.visibility = View.GONE
        } else if (item.commercialSender != null) {
            holder.binding.chatSender.text = "${item.commercialSender?.name}"
            holder.binding.chatOwner.visibility = View.VISIBLE
        }
        holder.binding.chatMessage.text = item.message
        holder.binding.chatTime.text = SimpleDateFormat("HH:mm", Locale.getDefault())
            .format(item.created)

        if (item.privateSender?.avatar != null) {
            val url = GET_MEDIA_ENDPOINT.toString() + "/" + item.privateSender?.avatar?.url
            Glide.with(holder.itemView)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.chatMessageSenderAvatar)
        } else {
            holder.binding.chatMessageSenderAvatar.setImageResource(R.drawable.default_avatar)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}