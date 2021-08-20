package com.ibri.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemSubscribeRequestBinding
import com.ibri.model.events.SubscribeRequest

class SubRequestAdapter(private val listener: NotificationClickListener) :
    RecyclerView.Adapter<SubRequestAdapter.SubRequestViewHolder>() {

    val subRequestsList = ArrayList<SubscribeRequest>()
    private var isAccepted = false

    fun setData(list: List<SubscribeRequest>) {
        subRequestsList.clear()
        subRequestsList.addAll(list)
        notifyItemRangeChanged(0, subRequestsList.size)
    }

    inner class SubRequestViewHolder(val binding: ItemSubscribeRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubRequestViewHolder {
        return SubRequestViewHolder(
            ItemSubscribeRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SubRequestViewHolder, position: Int) {
        val item = subRequestsList[position]

        holder.binding.subReqUserName.text = item.user.name + " " + item.user.surname
        holder.binding.subReqEventTitle.text = item.privateEvent.title

        holder.binding.subReqAccept.setOnClickListener {
            isAccepted = true
            listener.onAcceptSubRequestClickListener(item, isAccepted, position)
        }

        holder.binding.subReqDecline.setOnClickListener {
            isAccepted = false
            listener.onAcceptSubRequestClickListener(item, isAccepted, position)
        }
    }

    override fun getItemCount(): Int {
        return subRequestsList.size
    }
}