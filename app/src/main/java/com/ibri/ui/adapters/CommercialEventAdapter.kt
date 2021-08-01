package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemCommercialEventBinding
import com.ibri.model.events.CommercialEvent

class CommercialEventAdapter(
    private val context: Context
) : RecyclerView.Adapter<CommercialEventAdapter.CommercialEventViewHolder>() {

    private val commercialEventList = ArrayList<CommercialEvent>()

    fun setData(list: List<CommercialEvent>) {
        commercialEventList.clear()
        commercialEventList.addAll(list)
        notifyDataSetChanged()
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
        val item = commercialEventList[position]

        holder.binding.comEventTitle.text = item.title
        holder.binding.comEventCreator.text = item.creator.name
    }

    override fun getItemCount(): Int {
        return commercialEventList.size
    }


}