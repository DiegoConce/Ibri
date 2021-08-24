package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.Question
import com.ibri.repository.QuestionAnswerRepository

class QnAViewModel : ViewModel() {
    val questionList = MutableLiveData<ArrayList<Question>>()
    val newQuestionResponse = MutableLiveData<String>()

    fun sendQuestion(question: String, eventId: String) {
        QuestionAnswerRepository.MakeNewQuestion(newQuestionResponse, question, eventId)
    }

    fun getQuestions(eventId: String) {
        QuestionAnswerRepository.getQna(questionList, eventId)
    }
}