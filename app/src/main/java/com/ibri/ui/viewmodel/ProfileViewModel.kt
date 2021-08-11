package com.ibri.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.LoginResponse
import com.ibri.model.Question
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.repository.ProfileRepository
import com.ibri.repository.QuestionAnswerRepository

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

    val questionList = MutableLiveData<ArrayList<Question>>()
    val deleteQuestionResponse = MutableLiveData<String>()
    val answerQuestionResponse = MutableLiveData<String>()

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

    fun loadQuestions(userId: String) {
        QuestionAnswerRepository.getUncompletedQuestions(questionList, userId)
    }

    fun deleteQuestion(questionId: String) {
        QuestionAnswerRepository.deleteQuestion(deleteQuestionResponse, questionId)
    }

    fun answerQuestion(questionId: String, answer: String) {
        QuestionAnswerRepository.answerQuestion(answerQuestionResponse, questionId, answer)
    }

    override fun onCleared() {
        super.onCleared()
    }
}