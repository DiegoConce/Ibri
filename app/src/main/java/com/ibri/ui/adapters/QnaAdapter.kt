package com.ibri.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibri.databinding.ItemQnaBinding
import com.ibri.model.Question
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QnaAdapter(private val context: Context) : RecyclerView.Adapter<QnaAdapter.QnaViewHolder>() {

    private val questionsList = ArrayList<Question>()

    fun setData(list: List<Question>) {
        questionsList.clear()
        questionsList.addAll(list)
        notifyItemRangeChanged(0, questionsList.size)
    }

    inner class QnaViewHolder(val binding: ItemQnaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QnaViewHolder {
        return QnaViewHolder(
            ItemQnaBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: QnaViewHolder, position: Int) {
        val item = questionsList[position]
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        holder.binding.qnaQuestionText.text = item.question
        holder.binding.qnaAnswerText.text = item.answer
        holder.binding.qnaResponseTime.text = simpleDateFormat.format(item.responseDate!!)
    }

    override fun getItemCount(): Int {
        return questionsList.size
    }
}