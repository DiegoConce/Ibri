package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.events.CommercialEvent
import com.ibri.repository.CommercialEventRepository

class CommercialEventViewModel : ViewModel() {
    val comEventList = MutableLiveData<ArrayList<CommercialEvent>>()

    fun getCommercialEvents() = CommercialEventRepository.getCommercialEvent(comEventList)

    override fun onCleared() {
        CommercialEventRepository.closeVolley()
        super.onCleared()
    }
}