package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.LoginResponse
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.repository.ProfileRepository

class ProfileViewModel : ViewModel() {
    val standardEventList = MutableLiveData<ArrayList<StandardEvent>>()
    val comEventList = MutableLiveData<ArrayList<CommercialEvent>>()
    val accountResponse = MutableLiveData<LoginResponse>()
    val editUserResponse = MutableLiveData<String>()
    val editCompanyResponse = MutableLiveData<String>()
    var isMyProfile = MutableLiveData(true)
    var isCompany = MutableLiveData(false)

    var userId = ""
    var inputName = ""
    var inputSurname = ""
    var inputBio = ""


    fun getGuestProfile(userId: String) {
        ProfileRepository.getAccount(accountResponse, userId)
    }

    fun standardEventsByUser(userId: String) {
        standardEventList.value?.clear()
        ProfileRepository.getStandardEventsByUser(standardEventList, userId)
    }

    fun commercialEventsByUser(userId: String) {
        comEventList.value?.clear()
        ProfileRepository.getCommercialEventsByUser(comEventList, userId)
    }

    fun performEditUser() {
        ProfileRepository.editUser(
            editUserResponse,
            userId, inputName, inputSurname, inputBio
        )
    }

    fun performEditCompany() {
        ProfileRepository.editCompany(
            editCompanyResponse,
            userId, inputName, inputBio
        )
    }

    fun clearFields() {
        userId = ""
        inputName = ""
        inputSurname = ""
        inputBio = ""
    }

    override fun onCleared() {
        super.onCleared()
    }
}