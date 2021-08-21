package com.ibri.ui.viewmodel

import android.app.VoiceInteractor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.events.CommercialEvent
import com.ibri.repository.CommercialEventRepository

class CommercialEventViewModel : ViewModel() {
    val comEventList = MutableLiveData<ArrayList<CommercialEvent>>()
    val selectedCommercialEvent = MutableLiveData<CommercialEvent>()
    val newComEventResponse = MutableLiveData<String>()
    val deleteComEventResponse = MutableLiveData<String>()
    var isMyEvent = MutableLiveData(false)
    var isSubcribed = MutableLiveData(false)


    fun getCommercialEvents() = CommercialEventRepository.getCommercialEvent(comEventList)

    fun createCommercialEvent(
        userId: String,
        title: String,
        description: String,
        startDate: String,
        eventDate: String,
        maxSubscribers: Int,
        maxRooms: Int,
        lat: String,
        lon: String,
        address: String,
        city: String,
        tags: String,
        media: String
    ) {
        CommercialEventRepository.newCommercialEvent(
            newComEventResponse,
            userId,
            title,
            description,
            startDate,
            eventDate,
            maxSubscribers,
            maxRooms,
            lat,
            lon,
            address,
            city,
            tags,
            media
        )
    }

    fun subscribeToCommercialEvent(userId: String, eventId: String) {
        CommercialEventRepository.subscribeToCommercialEvent(
            selectedCommercialEvent,
            isSubcribed,
            userId,
            eventId
        )
    }

    fun cancelSubscription(userId: String, eventId: String) {
        CommercialEventRepository.unsubscribeToCommercialEvent(
            selectedCommercialEvent,
            isSubcribed,
            userId,
            eventId
        )
    }

    fun sendQuestion() {

    }

    fun getQuestions() {

    }

    fun editCommercialEvent() {

    }

    fun deleteCommercialEvent(eventId: String) {
        CommercialEventRepository.deleteCommercialEvent(deleteComEventResponse, eventId)
    }

    fun reloadEvent() {
        selectedCommercialEvent.value?.let {
            CommercialEventRepository.getCommercialEventById(selectedCommercialEvent, it.id)
        }
    }

}