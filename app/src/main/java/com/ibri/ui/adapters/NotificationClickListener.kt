package com.ibri.ui.adapters

import android.widget.ProgressBar
import com.ibri.model.Question

interface NotificationClickListener {
    fun onDeteleClickListener(question: Question, position: Int)
    fun onAnswerClickListener(
        question: Question,
        answer: String,
        position: Int,
        answerProgressBar: ProgressBar
    )
}