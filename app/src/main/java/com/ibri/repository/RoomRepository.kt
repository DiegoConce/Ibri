package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.messaging.Room
import com.ibri.utils.BASE_URL

class RoomRepository {
    companion object {
        private val NEW_ROOM_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/rooms/new")
        private val ENTER_ROOM_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/rooms/enter")
        private val LEAVE_ROOM_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/rooms/leave")
        private val DELETE_ROOM_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/rooms/delete")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())

        fun newRoom(
            mutableMedia: MutableLiveData<String>,
            eventId: String,
            name: String,
            description: String,
            maxMembers: Int,
            userId: String,
            media: String
        ) {
            val req = object : StringRequest(
                Method.POST, NEW_ROOM_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["eventId"] = eventId
                    map["name"] = name
                    map["description"] = description
                    map["maxMembers"] = maxMembers.toString()
                    map["creator"] = userId
                    map["media"] = media
                    return map
                }
            }
            volley.add(req)
        }

        fun enterRoom(
            mutableMedia: MutableLiveData<Room>,
            userId: String,
            roomId: String
        ) {
            val req = object : StringRequest(
                Method.POST, ENTER_ROOM_ENDPOINT.toString(),
                { result ->
                    val a = Gson().fromJson(result, Room::class.java)
                    mutableMedia.postValue(a)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["userId"] = userId
                    map["roomId"] = roomId
                    return map
                }
            }
            volley.add(req)
        }

        fun leaveRoom(mutableMedia: MutableLiveData<String>, userId: String, roomId: String) {
            val req = StringRequest(
                Request.Method.GET, LEAVE_ROOM_ENDPOINT.buildUpon()
                    .appendQueryParameter("user", userId)
                    .appendQueryParameter("room", roomId)
                    .build().toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            )
            volley.add(req)
        }

        fun deleteRoom(mutableMedia: MutableLiveData<String>, roomId: String) {
            val req = StringRequest(
                Request.Method.GET, DELETE_ROOM_ENDPOINT.buildUpon()
                    .appendQueryParameter("room", roomId)
                    .build().toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            )
            volley.add(req)
        }
    }
}