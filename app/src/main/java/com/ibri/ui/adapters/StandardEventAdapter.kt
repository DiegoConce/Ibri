package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemStandardEventBinding
import com.ibri.model.events.StandardEvent
import com.ibri.ui.event.SelectedItemListener

class StandardEventAdapter(
    private val context: Context,
    private val listener: SelectedItemListener
) :
    RecyclerView.Adapter<StandardEventAdapter.StandardEventViewHolder>() {

    private val standardEventList = ArrayList<StandardEvent>()

    fun setData(list: List<StandardEvent>) {
        standardEventList.clear()
        standardEventList.addAll(list)
        notifyDataSetChanged()
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
        val item = standardEventList[position]

        holder.binding.standEventCreator.text = item.creator.name
        holder.binding.standEventTitle.text = item.title

        setListeners(holder, item)
    }

    private fun setListeners(holder: StandardEventViewHolder, item: StandardEvent) {

        holder.binding.standEventCard.setOnClickListener {
            listener.onItemSelected(item)
        }

    }

    override fun getItemCount(): Int {
        return standardEventList.size
    }

}