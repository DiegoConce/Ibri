package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.Question
import com.ibri.model.events.StandardEvent
import com.ibri.repository.QuestionAnswerRepository
import com.ibri.repository.StandardEventRepository
import kotlin.collections.ArrayList

class StandardEventViewModel : ViewModel() {
    val eventList = MutableLiveData<ArrayList<StandardEvent>>()
    val selectedStandardEvent = MutableLiveData<StandardEvent>()
    val newStandEventResponse = MutableLiveData<String>()
    var isMyEvent = MutableLiveData(false)
    var isSubcribed = MutableLiveData(false)
    val newQuestionResponse = MutableLiveData<String>()
    val questionList = MutableLiveData<ArrayList<Question>>()


    fun getStandardEvents() = StandardEventRepository.getStandardEvent(eventList)

    fun createStandardEvent(
        userId: String,
        title: String,
        description: String,
        startDate: String,
        eventDate: String,
        maxSubscribers: Int,
        lat: String,
        lon: String,
        address: String,
        city: String,
        tags: String,
        isPrivate: Boolean,
        media: String
    ) {
        StandardEventRepository.newStandardEvent(
            newStandEventResponse,
            userId,
            title,
            description,
            startDate,
            eventDate,
            maxSubscribers,
            lat,
            lon,
            address,
            city,
            tags,
            isPrivate,
            media
        )
    }

    fun subscribeToEventRequest(
        userId: String,
        eventId: String
    ) {
        StandardEventRepository.subscribeToStandardEvent(
            selectedStandardEvent,
            isSubcribed,
            userId,
            eventId
        )
    }

    fun cancelSubscription(
        userId: String,
        eventId: String
    ) {
        StandardEventRepository.unsubscribeToStandardEvent(
            selectedStandardEvent,
            isSubcribed,
            userId,
            eventId
        )
    }

    fun sendQuestion(question: String, eventId: String) {
        QuestionAnswerRepository.MakeNewQuestion(newQuestionResponse, question, eventId)
    }

    fun getQuestions(eventId: String) {
        QuestionAnswerRepository.getQna(questionList, eventId)
    }

    override fun onCleared() {
        super.onCleared()
    }
}