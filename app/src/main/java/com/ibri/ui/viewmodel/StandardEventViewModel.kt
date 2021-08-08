package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.Media
import com.ibri.model.events.StandardEvent
import com.ibri.repository.StandardEventRepository
import kotlin.collections.ArrayList

class StandardEventViewModel : ViewModel() {
    val eventList = MutableLiveData<ArrayList<StandardEvent>>()
    val selectedStandardEvent = MutableLiveData<StandardEvent>()
    val newStandEventResponse = MutableLiveData<String>()

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

    override fun onCleared() {
        super.onCleared()
    }
}