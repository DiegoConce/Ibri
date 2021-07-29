package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.events.StandardEvent
import com.ibri.repository.StandardEventRepository
import kotlin.collections.ArrayList

class StandardEventViewModel : ViewModel() {
    val eventList = MutableLiveData<ArrayList<StandardEvent>>()
    val selectedStandardEvent = MutableLiveData<StandardEvent>()

    fun getStandardEvents() = StandardEventRepository.getStandardEvent(eventList)

    override fun onCleared() {
        StandardEventRepository.closeVolley()
        super.onCleared()
    }
}