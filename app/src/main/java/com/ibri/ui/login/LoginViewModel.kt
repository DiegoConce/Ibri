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
    val registerUserResponse = MutableLiveData<String>()
    val registerCompanyResponse = MutableLiveData<String>()

    fun performLogin(email: String, password: String) =
        LoginRepository.findAccount(loginResponse, email, password)


    fun performRegisterUser(
        name: String,
        surname: String,
        birthday: String,
        gender: String,
        email: String,
        password: String
    ) =
        LoginRepository.registerAccount(
            registerUserResponse,
            name,
            surname,
            birthday,
            gender,
            email,
            password
        )


    fun performRegisterCompany(name: String, email: String, pIva: String, password: String) =
        LoginRepository.registerCompany(registerCompanyResponse, name, email, pIva, password)

}