package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.Question
import com.ibri.model.events.StandardEvent
import com.ibri.model.messaging.Message
import com.ibri.repository.ChatRepository
import com.ibri.repository.QuestionAnswerRepository
import com.ibri.repository.StandardEventRepository
import kotlin.collections.ArrayList

class StandardEventViewModel : ViewModel() {
    val eventList = MutableLiveData<ArrayList<StandardEvent>>()
    val questionList = MutableLiveData<ArrayList<Question>>()
    val messagesList = MutableLiveData<ArrayList<Message>>()
    val selectedStandardEvent = MutableLiveData<StandardEvent>()
    val newStandEventResponse = MutableLiveData<String>()
    val editStandEventResponse = MutableLiveData<String>()
    val deleteStandEventResponse = MutableLiveData<String>()
    val standEventMessageResponse = MutableLiveData<String>()
    var isMyEvent = MutableLiveData(false)
    var isSubcribed = MutableLiveData(false)
    val newQuestionResponse = MutableLiveData<String>()


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

    fun editStandardEvent(
        description: String,
        startDate: String,
        eventDate: String,
        maxSubscribers: Int,
        lat: String,
        lon: String,
        address: String,
        city: String,
        isPrivate: Boolean,
        media: String?,
        eventId: String
    ) {
        StandardEventRepository.updateStandardEvent(
            editStandEventResponse,
            description,
            startDate,
            eventDate,
            maxSubscribers,
            lat,
            lon,
            address,
            city,
            isPrivate,
            media,
            eventId
        )
    }

    fun deleteStandardEvent(eventId: String) {
        StandardEventRepository.deleteStandardEvent(deleteStandEventResponse, eventId)
    }

    fun reloadEvent() {
        selectedStandardEvent.value?.let {
            StandardEventRepository.getStandardEventById(
                selectedStandardEvent,
                it.id
            )
        }
    }

    fun fetchMessages(chatId: String) {
        ChatRepository.getMessages(messagesList, chatId)
    }

    fun sendMessage(chatId: String, message: String, sender: String) {
        ChatRepository.sendStandEventMessage(standEventMessageResponse, chatId, message, sender)
    }

    override fun onCleared() {
        super.onCleared()
    }
}