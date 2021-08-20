package com.ibri.ui.adapters

import android.widget.ProgressBar
import com.ibri.model.Question
import com.ibri.model.events.SubscribeRequest

interface NotificationClickListener {
    fun onDeteleClickListener(question: Question, position: Int)

    fun onAnswerClickListener(
        question: Question,
        answer: String,
        position: Int,
        answerProgressBar: ProgressBar
    )

    fun onAcceptSubRequestClickListener(
        subscribeRequest: SubscribeRequest,
        isAccepted: Boolean,
        position: Int
    )

}