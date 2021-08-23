package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent
import com.ibri.utils.BASE_URL

class CommercialEventRepository {

    companion object {
        private val COMMERCIAL_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/all")
        private val COMMERCIAL_POSITION_EVENTS_ENDPOINT =
            Uri.parse("${BASE_URL}/event/commercial/all/position")
        private val NEW_COMMERCIAL_EVENT_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/new")
        private val GET_COMMERCIAL_EVENT_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/get/id")
        private val COMMERCIAL_EVENT_SUBSCRIBE_ENDPOINT =
            Uri.parse("${BASE_URL}/event/commercial/subscribe")
        private val COMMERCIAL_EVENT_UNSUBSCRIBE_ENDPOINT =
            Uri.parse("${BASE_URL}/event/commercial/unsubscribe")
        private val DELETE_COMMERCIAL_EVENT_ENDPOINT =
            Uri.parse("${BASE_URL}/event/commercial/delete")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())

        fun getCommercialEvent(mutableMediaList: MutableLiveData<ArrayList<CommercialEvent>>) {
            val req = StringRequest(
                Request.Method.GET, COMMERCIAL_EVENTS_ENDPOINT.toString(),
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

        fun getCommercialEventsByPosition(
            mutableMediaList: MutableLiveData<ArrayList<CommercialEvent>>,
            lat: String,
            lon: String,
            distanceInM: Int
        ) {
            val req = StringRequest(
                Request.Method.GET, COMMERCIAL_POSITION_EVENTS_ENDPOINT.buildUpon()
                    .appendQueryParameter("lat", lat)
                    .appendQueryParameter("lon", lon)
                    .appendQueryParameter("distanceInM", distanceInM.toString())
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

        fun newCommercialEvent(
            mutableMedia: MutableLiveData<String>,
            userId: String,
            title: String,
            description: String,
            startDate: String,
            eventDate: String,
            maxSubscribers: Int,
            maxRooms: Int,
            lat: String,
            lon: String,
            address: String,
            city: String,
            tags: String,
            media: String
        ) {
            val req = object : StringRequest(
                Method.POST, NEW_COMMERCIAL_EVENT_ENDPOINT.toString(),
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
                    map["maxRooms"] = maxRooms.toString()
                    map["lat"] = lat
                    map["lon"] = lon
                    map["address"] = address
                    map["city"] = city
                    map["tags"] = tags
                    map["media"] = media
                    return map
                }
            }
            volley.add(req)
        }

        fun subscribeToCommercialEvent(
            selectedCommercialEvent: MutableLiveData<CommercialEvent>,
            isSubscribed: MutableLiveData<Boolean>,
            userId: String,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, COMMERCIAL_EVENT_SUBSCRIBE_ENDPOINT.toString(),
                { result ->
                    isSubscribed.value = true
                    val a = Gson().fromJson(result, CommercialEvent::class.java)
                    selectedCommercialEvent.postValue(a)
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

        fun unsubscribeToCommercialEvent(
            selectedCommercialEvent: MutableLiveData<CommercialEvent>,
            isSubscribed: MutableLiveData<Boolean>,
            userId: String,
            eventId: String
        ) {
            val req = object : StringRequest(
                Method.POST, COMMERCIAL_EVENT_UNSUBSCRIBE_ENDPOINT.toString(),
                { result ->
                    isSubscribed.value = false
                    val a = Gson().fromJson(result, CommercialEvent::class.java)
                    selectedCommercialEvent.postValue(a)
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

        fun updateCommercialEvent() {

        }

        fun deleteCommercialEvent(
            mutableMedia: MutableLiveData<String>,
            eventId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                DELETE_COMMERCIAL_EVENT_ENDPOINT.buildUpon().appendQueryParameter("event", eventId)
                    .build().toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            )
            volley.add(req)
        }

        fun getCommercialEventById(
            mutableMedia: MutableLiveData<CommercialEvent>,
            eventId: String
        ) {
            val req = StringRequest(
                Request.Method.GET,
                GET_COMMERCIAL_EVENT_ENDPOINT.buildUpon().appendQueryParameter("eventId", eventId)
                    .build().toString(),
                { result ->
                    val a = Gson().fromJson(result, CommercialEvent::class.java)
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