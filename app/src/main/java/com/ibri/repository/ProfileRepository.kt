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
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.model.events.SubscribedEventResponse
import com.ibri.utils.BASE_URL
import com.ibri.utils.LOG_TEST

class ProfileRepository {

    companion object {
        private val ACCOUNT_ENDPOINT = Uri.parse("${BASE_URL}/account/get")
        private val SUBSCRIBED_EVENTS_BY_USER_ENDPOINT =
            Uri.parse("${BASE_URL}/event/get/user/subscribed")
        private val COMMERCIAL_EVENTS_BY_USER_ENDPOINT = Uri.parse("${BASE_URL}/event/get/company")
        private val EVENTS_BY_USER_ENDPOINT = Uri.parse("${BASE_URL}/event/get/user")
        private val COMPANY_EDIT_ENDPOINT = Uri.parse("${BASE_URL}/company/profile/edit")
        private val USER_EDIT_ENDPOINT = Uri.parse("${BASE_URL}/user/profile/edit")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        fun getAccount(
            mutableMedia: MutableLiveData<LoginResponse>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                ACCOUNT_ENDPOINT.buildUpon().appendQueryParameter("id", userId).build().toString(),
                { result ->
                    val a = Gson().fromJson(result, LoginResponse::class.java)
                    mutableMedia.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getStandardEventsByUser(
            mutableMediaList: MutableLiveData<ArrayList<StandardEvent>>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                EVENTS_BY_USER_ENDPOINT.buildUpon().appendQueryParameter("id", userId).build()
                    .toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<StandardEvent>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getCommercialEventsByUser(
            mutableMediaList: MutableLiveData<ArrayList<CommercialEvent>>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                COMMERCIAL_EVENTS_BY_USER_ENDPOINT.buildUpon().appendQueryParameter("id", userId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<CommercialEvent>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getSubscribedEvents(
            mutableMediaList: MutableLiveData<ArrayList<SubscribedEventResponse>>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                SUBSCRIBED_EVENTS_BY_USER_ENDPOINT.buildUpon().appendQueryParameter("id", userId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<SubscribedEventResponse>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun editUser(
            mutableMedia: MutableLiveData<String>,
            userId: String,
            name: String,
            surname: String,
            bio: String
        ) {
            val req = object : StringRequest(
                Method.POST, USER_EDIT_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = userId
                    map["name"] = name
                    map["surname"] = surname
                    map["bio"] = bio
                    return map
                }
            }
            volley.add(req)
        }

        fun editCompany(
            mutableMedia: MutableLiveData<String>,
            userId: String,
            name: String,
            bio: String
        ) {
            val req = object : StringRequest(
                Method.POST, COMPANY_EDIT_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["id"] = userId
                    map["name"] = name
                    map["bio"] = bio
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