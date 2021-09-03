package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemRoomBinding
import com.ibri.model.messaging.Room
import com.ibri.utils.GET_MEDIA_ENDPOINT

class RoomAdapter(
    private val context: Context,
    private val listener: RoomOnClickListener
) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private val roomList = ArrayList<Room>()

    fun setData(list: List<Room>) {
        roomList.clear()
        roomList.addAll(list)
        notifyItemRangeChanged(0, roomList.size)
    }

    inner class RoomViewHolder(val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            ItemRoomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val item = roomList[position]

        holder.binding.itemRoomName.text = item.name
        holder.binding.roomPartecipants.text = "${item.members.size} / ${item.maxMembers}"

        if (item.image?.url != null) {
            val url = GET_MEDIA_ENDPOINT.toString() + "/" + item.image?.url
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.itemRoomAvatar)
        } else {
            holder.binding.itemRoomAvatar.setImageResource(R.drawable.default_avatar)
        }

        holder.binding.roomCard.setOnClickListener { listener.onRoomClicked(item) }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }

}