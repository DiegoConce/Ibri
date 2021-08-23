package com.ibri.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemTagBinding
import com.ibri.model.Tag

class TagAdapter(
    private val listener: TagOnClickListener
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private val tagList = ArrayList<Tag>()

    fun setData(list: List<Tag>) {
        tagList.clear()
        tagList.addAll(list)
        notifyItemRangeChanged(0, tagList.size)
    }

    inner class TagViewHolder(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(
            ItemTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val item = tagList[position]

        holder.binding.tagName.text = item.name
        holder.binding.tagCard.setOnClickListener { listener.onTagCliked(item) }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

}