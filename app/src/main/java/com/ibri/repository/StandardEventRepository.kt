package com.ibri.repository


import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.Media
import com.ibri.model.events.StandardEvent
import com.ibri.utils.BASE_URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StandardEventRepository {

    companion object {
        private val ALL_STANDARD_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/private/all")
        private val NEW_PRIVATE_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/private/new")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        fun getStandardEvent(mutableMediaList: MutableLiveData<ArrayList<StandardEvent>>) {
            val req = StringRequest(
                Request.Method.GET, ALL_STANDARD_EVENTS_ENDPOINT.toString(),
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

        fun newStandardEvent(
            mutableMedia: MutableLiveData<String>,
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
            val req = object : StringRequest(
                Method.POST, NEW_PRIVATE_EVENTS_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["userid"] = userId
                    map["title"] = title
                    map["description"] = description
                    map["startDate"] = startDate
                    map["eventDate"] = eventDate
                    map["maxSubscribers"] = maxSubscribers.toString()
                    map["lat"] = lat
                    map["lon"] = lon
                    map["address"] = address
                    map["city"] = city
                    map["tags"] = tags
                    map["isPrivate"] = isPrivate.toString()
                    map["media"] = media
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