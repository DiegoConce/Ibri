package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemStandardEventBinding
import com.ibri.model.events.StandardEvent
import com.ibri.utils.GET_MEDIA_ENDPOINT
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StandardEventAdapter(
    private val context: Context,
    private val listener: EventsOnClickListener
) :
    RecyclerView.Adapter<StandardEventAdapter.StandardEventViewHolder>() {

    private val standardEventList = ArrayList<StandardEvent>()
    private val unfilteredList = ArrayList<StandardEvent>()
    private var isProfileLayout = false

    fun setData(list: List<StandardEvent>) {
        standardEventList.clear()
        standardEventList.addAll(list)
        unfilteredList.clear()
        unfilteredList.addAll(list)
        notifyItemRangeChanged(0, standardEventList.size)
    }

    inner class StandardEventViewHolder(val binding: ItemStandardEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandardEventViewHolder {
        return StandardEventViewHolder(
            ItemStandardEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StandardEventViewHolder, position: Int) {
        val calendar: Calendar = Calendar.getInstance()
        val item = standardEventList[position]
        calendar.time = item.eventDay

        holder.binding.standEventCreator.text = item.creator.name
        holder.binding.standEventTitle.text = item.title

        if (item.creator.avatar != null) {
            val path = item.creator.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.standEventCreatorAvatar)
        } else {
            holder.binding.standEventCreatorAvatar.setImageResource(R.drawable.default_avatar)
        }

        holder.binding.standEventLocation.text = item.city
        holder.binding.standEventMembers.text = "${item.guests} / ${item.maxGuests}"
        holder.binding.standEventDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
        holder.binding.standEventMonth.text = SimpleDateFormat("MMM", Locale.getDefault())
            .format(calendar.time)

        if (item.media != null) {
            val path = item.media!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.standEventImage)
        }

        if (isProfileLayout) {
            holder.binding.standEventAvatarCard.visibility = View.GONE
            holder.binding.standEventCreator.visibility = View.GONE
        } else {
            holder.binding.standEventAvatarCard.visibility = View.VISIBLE
            holder.binding.standEventCreator.visibility = View.VISIBLE
        }

        setListeners(holder, item)
    }

    private fun setListeners(holder: StandardEventViewHolder, item: StandardEvent) {

        holder.binding.standEventCard.setOnClickListener {
            listener.onItemSelected(item)
        }

        holder.binding.standEventCreator.setOnClickListener {
            listener.onCreatorSelected(item.creator.id)
        }

        holder.binding.standEventCreatorAvatar.setOnClickListener {
            listener.onCreatorSelected(item.creator.id)
        }

    }

    override fun getItemCount(): Int {
        return standardEventList.size
    }

    fun setProfileLayout() {
        isProfileLayout = true
    }

    fun filter(s: String) {
        var query = s
        standardEventList.clear()
        if (query.isEmpty()) {
            standardEventList.addAll(unfilteredList)
        } else {
            query = query.lowercase()
            for (item in unfilteredList) {
                val fullName = item.creator.name + item.creator.surname

                if (item.title.lowercase().contains(query) || fullName.lowercase()
                        .contains(query)
                ) {
                    standardEventList.add(item)
                }
            }
        }

        notifyDataSetChanged()
    }

}