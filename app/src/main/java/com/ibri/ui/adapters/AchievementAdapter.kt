package com.ibri.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemAchievementBinding
import com.ibri.model.Achievement
import com.ibri.utils.GET_MEDIA_ENDPOINT

class AchievementAdapter : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    private val achievements = ArrayList<Achievement>()

    fun setData(list: List<Achievement>) {
        achievements.clear()
        achievements.addAll(list)
        notifyItemRangeChanged(0, achievements.size)
    }

    inner class AchievementViewHolder(val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        return AchievementViewHolder(
            ItemAchievementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val item = achievements[position]

        holder.binding.achievementTitle.text = item.title
        holder.binding.achievementProgress.text = item.description

        if (item.image != null) {
            val url = GET_MEDIA_ENDPOINT.toString() + "/" + item.image?.url
            Glide.with(holder.itemView)
                .load(url)
                .error(R.drawable.default_background)
                .into(holder.binding.achievementImage)
        } else {
            holder.binding.achievementImage.setImageResource(R.drawable.default_background)
        }

        if (item.unlocked)
            holder.binding.unlockIcon.visibility = View.VISIBLE
        else {
            holder.binding.achievementItemLayout.alpha = 0.4F
            holder.binding.unlockIcon.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return achievements.size
    }
}