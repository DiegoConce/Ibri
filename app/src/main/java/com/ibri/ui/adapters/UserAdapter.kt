package com.ibri.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibri.R
import com.ibri.databinding.ItemUserBinding
import com.ibri.model.User
import com.ibri.utils.GET_MEDIA_ENDPOINT

class UserAdapter(
    private val context: Context,
    private val listener: UserOnClickListener
) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val userList = ArrayList<User>()

    fun setData(list: List<User>) {
        userList.clear()
        userList.addAll(list)
        notifyItemRangeChanged(0, userList.size)
    }

    inner class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = userList[position]

        holder.binding.userCreatorName.text = item.name + " " + item.surname
        if (item.avatar != null) {
            val path = item.avatar!!.url
            val url = "$GET_MEDIA_ENDPOINT/$path"
            Glide.with(context)
                .load(url)
                .error(R.drawable.default_avatar)
                .into(holder.binding.userAvatar)
        } else
            holder.binding.userAvatar.setImageResource(R.drawable.default_avatar)


        holder.binding.userAvatar.setOnClickListener { listener.onUserClicked(item.id) }
        holder.binding.userCreatorName.setOnClickListener { listener.onUserClicked(item.id) }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}

