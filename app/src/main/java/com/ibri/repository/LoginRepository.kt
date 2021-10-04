package com.ibri.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.LoginResponse
import com.ibri.utils.BASE_URL
import com.ibri.utils.LOG_TEST
import java.math.BigInteger
import java.security.MessageDigest

class LoginRepository {

    companion object {
        private val LOGIN_ENDPOINT = Uri.parse("${BASE_URL}/account/in")
        private val USER_REGISTER_ENDPOINT = Uri.parse("${BASE_URL}/account/user/signup")
        private val COMPANY_REGISTER_ENDPOINT = Uri.parse("${BASE_URL}/account/commercial/signup")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())

        fun findAccount(
            mutableMedia: MutableLiveData<LoginResponse>,
            email: String,
            password: String
        ) {
            val req = object : StringRequest(
                Method.POST, LOGIN_ENDPOINT.toString(),
                { result ->
                    val a = Gson().fromJson(result, LoginResponse::class.java)
                    mutableMedia.postValue(a)
                },
                { error ->

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["email"] = email
                    map["password"] = md5(password)

                    return map
                }
            }

            volley.add(req)
        }

        private fun md5(input: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }


        fun registerAccount(
            mutableMedia: MutableLiveData<String>,
            name: String,
            surname: String,
            birthday: String,
            gender: String,
            email: String,
            password: String,
        ) {
            val req = object : StringRequest(
                Method.POST, USER_REGISTER_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {
                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["name"] = name
                    map["surname"] = surname
                    map["gender"] = gender
                    map["birthday"] = birthday
                    map["email"] = email
                    map["password"] = md5(password)
                    return map
                }
            }

            volley.add(req)
        }

        fun registerCompany(
            mutableMedia: MutableLiveData<String>,
            name: String,
            email: String,
            pIva: String,
            password: String
        ) {
            val req = object : StringRequest(
                Method.POST, COMPANY_REGISTER_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["name"] = name
                    map["email"] = email
                    map["pIva"] = pIva
                    map["password"] = md5(password)
                    return map
                }
            }

            volley.add(req)
        }

        fun closeVolley() {
            volley.stop()
        }
    }
}