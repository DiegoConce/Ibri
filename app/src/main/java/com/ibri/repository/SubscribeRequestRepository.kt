package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.events.SubscribeRequest
import com.ibri.utils.BASE_URL

class SubscribeRequestRepository {
    companion object {
        private val GET_SUBSCRIBE_REQUESTS_ENDPOINT =
            Uri.parse("${BASE_URL}/event/private/subrequests")
        private val ACCEPT_SUBSCRIBE_REQUEST_ENDPOINT =
            Uri.parse("${BASE_URL}/event/private/subrequest/accept")

        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        fun getSubscribeRequests(
            mutableMediaList: MutableLiveData<ArrayList<SubscribeRequest>>,
            userId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                GET_SUBSCRIBE_REQUESTS_ENDPOINT.buildUpon().appendQueryParameter("userId", userId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<SubscribeRequest>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun acceptSubscribeRequest(
            mutableMedia: MutableLiveData<String>,
            subRequestId: String,
            isAccepted: Boolean
        ) {
            val req = object : StringRequest(
                Method.POST, ACCEPT_SUBSCRIBE_REQUEST_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["subRequestId"] = subRequestId
                    map["isAccepted"] = isAccepted.toString()
                    return map
                }
            }
            volley.add(req)
        }
    }
}