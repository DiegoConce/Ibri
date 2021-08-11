package com.ibri.ui.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemNotificationQuestionBinding
import com.ibri.model.Question

class NotificationAdapter(
    private val context: Context,
    private val listener: NotificationClickListener
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    val questionsListAdapter = ArrayList<Question>()
    private var answer = ""

    fun setData(list: List<Question>) {
        questionsListAdapter.clear()
        questionsListAdapter.addAll(list)
        notifyItemRangeChanged(0, questionsListAdapter.size)
    }


    inner class NotificationViewHolder(val binding: ItemNotificationQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = questionsListAdapter[position]
        holder.binding.notificationQuestionText.text = item.question
        holder.binding.notificationQuestionEventTitle.text = item.privateEvent.title


        holder.binding.answerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                answer = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        holder.binding.notificationDeleteButton.setOnClickListener {
            listener.onDeteleClickListener(item, position)
        }
        holder.binding.notificationAnswerButton.setOnClickListener {
            holder.binding.answerProgressBar.visibility = View.GONE
            if (answer.isNotEmpty())
                listener.onAnswerClickListener(item, answer, position, holder.binding.answerProgressBar)
        }
    }

    override fun getItemCount(): Int {
        return questionsListAdapter.size
    }
}