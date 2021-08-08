package com.ibri.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.LoginResponse
import com.ibri.repository.LoginRepository
import com.ibri.utils.LOG_TEST

class LoginViewModel : ViewModel() {
    var loginResponse = MutableLiveData<LoginResponse>()
    val loginSuccess = MutableLiveData<Boolean>()
    val registerUserSuccess = MutableLiveData<String>()
    val registerCompanySuccess = MutableLiveData<String>()

    var inputEmail: String = ""
    var inputPassword: String = ""
    var inputName: String = ""
    var inputSurname: String = ""
    var inputGender: String = ""
    var inputBirthday: String = ""
    var inputPiva: String = ""


    fun performLogin() {
        Log.wtf(LOG_TEST,"Perform Login")
        LoginRepository.findAccount(loginResponse, inputEmail, inputPassword)
    }

    fun performRegisterUser() {
        LoginRepository.registerAccount(
            registerUserSuccess,
            inputName,
            inputSurname,
            inputBirthday,
            inputEmail,
            inputGender,
            inputPassword
        )
    }

    fun performRegisterCompany() {
        LoginRepository.registerCompany(
            registerCompanySuccess,
            inputName,
            inputEmail,
            inputPiva,
            inputPassword
        )
    }

    fun clearInputFields() {
        inputEmail = ""
        inputPassword = ""
        inputName = ""
        inputSurname = ""
        inputGender = ""
        inputBirthday = ""
        inputPiva = ""
    }

    override fun onCleared() {
        super.onCleared()
    }
}