package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemCommercialEventBinding
import com.ibri.model.events.CommercialEvent
import com.ibri.ui.event.SelectedEventListener
import com.ibri.utils.GET_MEDIA_ENDPOINT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CommercialEventAdapter(
    private val context: Context,
    private val listener: SelectedEventListener
) : RecyclerView.Adapter<CommercialEventAdapter.CommercialEventViewHolder>() {

    private val commercialEventList = ArrayList<CommercialEvent>()

    fun setData(list: List<CommercialEvent>) {
        commercialEventList.clear()
        commercialEventList.addAll(list)
        notifyItemRangeChanged(0, commercialEventList.size)
    }

    inner class CommercialEventViewHolder(val binding: ItemCommercialEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommercialEventViewHolder {
        return CommercialEventViewHolder(
            ItemCommercialEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommercialEventViewHolder, position: Int) {
        val calendar: Calendar = Calendar.getInstance()
        val item = commercialEventList[position]
        calendar.time = item.eventDay

        var rooms = "0"
        if (!item.rooms.isNullOrEmpty())
            rooms = item.rooms.size.toString()

        holder.binding.comEventTitle.text = item.title
        holder.binding.comEventCreator.text = item.creator.name

        if (item.creator.avatar != null) {
            val path = item.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.comEventCreatorAvatar)
        } else {
            holder.binding.comEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        }

        holder.binding.comEventCity.text = item.city
        holder.binding.comEventLocation.text = item.address
        holder.binding.comEventMembers.text = "${item.guests} / ${item.maxGuests}"
        holder.binding.comEventRooms.text = rooms + " / ${item.maxRooms}"
        holder.binding.comEventDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.binding.comEventMonth.text = SimpleDateFormat("MMM", Locale.getDefault())
            .format(calendar.time)

        if (item.media != null) {
            val path = item.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.comEventImage)
        }


        setListeners(holder, item)
    }

    private fun setListeners(holder: CommercialEventViewHolder, item: CommercialEvent) {
        holder.binding.comEventCreator.setOnClickListener {
            listener.onCreatorSelected(item.creator.id)
        }

        holder.binding.comEventCard.setOnClickListener {
            listener.onItemSelected(item)
        }
    }

    override fun getItemCount(): Int {
        return commercialEventList.size
    }


}