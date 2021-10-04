package com.ibri.repository


import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.events.StandardEvent
import com.ibri.utils.BASE_URL
import com.ibri.utils.LOG_TEST
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KFunction1

class StandardEventRepository {

    companion object {
        private val ALL_STANDARD_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/private/all")
        private val PRIVATE_POSITION_EVENTS_ENDPOINT =
            Uri.parse("${BASE_URL}/event/private/all/position")
        private val NEW_PRIVATE_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/private/new")
        private val PRIVATE_EVENT_SUBSCRIBE_ENDPOINT =
            Uri.parse("${BASE_URL}/event/private/subscribe")
        private val PRIVATE_EVENT_UNSUBSCRIBE_ENDPOINT =
            Uri.parse("${BASE_URL}/event/private/unsubscribe")
        private val UPDATE_PRIVATE_EVENT_ENDPOINT = Uri.parse("${BASE_URL}/event/private/update")
        private val PRIVATE_EVENT_DELETE_ENDPOINT = Uri.parse("${BASE_URL}/event/private/delete")
        private val PRIVATE_EVENT_BY_ID_ENDPOINT = Uri.parse("${BASE_URL}/event/private/get")

        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        //GetAllStandardEvents
        fun getStandardEvent(
            mutableMediaList: MutableLiveData<ArrayList<StandardEvent>>,
            funError: (VolleyError) -> Unit,
        ) {
            val req = StringRequest(
                Request.Method.GET, ALL_STANDARD_EVENTS_ENDPOINT.toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<StandardEvent>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                { throwable ->
                    funError(throwable)
                    Log.wtf(LOG_TEST, "Volley error : ${throwable}")
                }
            )
            volley.add(req)
        }

        fun getStandEventsByPosition(
            mutableMediaList: MutableLiveData<ArrayList<StandardEvent>>,
            lat: String,
            lon: String,
            distanceInM: Int
        ) {
            val req = StringRequest(
                Request.Method.GET, PRIVATE_POSITION_EVENTS_ENDPOINT.buildUpon()
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lon", lon)
                    .appendQueryParameter("distanceInM", distanceInM.toString())
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<StandardEvent>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {
                    Log.wtf(LOG_TEST, "Volley error : ${it}")
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


        fun subscribeToStandardEvent(
            selectedStandardEvent: MutableLiveData<StandardEvent>,
            isSubcribed: MutableLiveData<Boolean>,
            userId: String,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, PRIVATE_EVENT_SUBSCRIBE_ENDPOINT.toString(),
                { result ->

                    if (!selectedStandardEvent.value?.private!!)
                        isSubcribed.value = true

                    val a = Gson().fromJson(result, StandardEvent::class.java)
                    selectedStandardEvent.postValue(a)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["eventId"] = eventId
                    map["userId"] = userId
                    return map
                }
            }
            volley.add(req)
        }

        fun unsubscribeToStandardEvent(
            selectedStandardEvent: MutableLiveData<StandardEvent>,
            isSubcribed: MutableLiveData<Boolean>,
            userId: String,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, PRIVATE_EVENT_UNSUBSCRIBE_ENDPOINT.toString(),
                { result ->
                    isSubcribed.value = false
                    val a = Gson().fromJson(result, StandardEvent::class.java)
                    selectedStandardEvent.postValue(a)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["eventId"] = eventId
                    map["userId"] = userId
                    return map
                }
            }
            volley.add(req)
        }

        fun updateStandardEvent(
            mutableMedia: MutableLiveData<String>,
            description: String,
            startDate: String,
            eventDate: String,
            maxSubscribers: Int,
            lat: String,
            lon: String,
            address: String,
            city: String,
            isPrivate: Boolean,
            media: String?,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, UPDATE_PRIVATE_EVENT_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["description"] = description
                    map["startDate"] = startDate
                    map["eventDate"] = eventDate
                    map["maxSubscribers"] = maxSubscribers.toString()
                    map["lat"] = lat
                    map["lon"] = lon
                    map["address"] = address
                    map["city"] = city
                    map["isPrivate"] = isPrivate.toString()
                    map["eventId"] = eventId
                    map["media"] = media!!
                    return map
                }
            }
            volley.add(req)
        }

        fun deleteStandardEvent(mutableMedia: MutableLiveData<String>, eventId: String) {
            val req = StringRequest(
                Request.Method.GET,
                PRIVATE_EVENT_DELETE_ENDPOINT.buildUpon().appendQueryParameter("event", eventId)
                    .build().toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getStandardEventById(mutableMedia: MutableLiveData<StandardEvent>, eventId: String) {
            val req = StringRequest(
                Request.Method.GET,
                PRIVATE_EVENT_BY_ID_ENDPOINT.buildUpon().appendQueryParameter("eventId", eventId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, StandardEvent::class.java)
                    mutableMedia.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun closeVolley() {
            volley.stop()
        }
    }

}