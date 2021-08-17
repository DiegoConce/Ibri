package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.messaging.Message
import com.ibri.utils.BASE_URL

class ChatRepository {
    companion object {
        private val CHAT_ENDPOINT: Uri = Uri.parse("${BASE_URL}/messaging/chat")
        private val CHAT_SEND_PRIVATE_MESSAGE_ENDPOINT: Uri =
            Uri.parse("${BASE_URL}/messaging/chat/send/private/message")
        private val CHAT_SEND_COMMERCIAL_MESSAGE_ENDPOINT: Uri =
            Uri.parse("${BASE_URL}/messaging/chat/send/commercial/message")

        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        fun getMessages(mutableMediaList: MutableLiveData<ArrayList<Message>>, chatId: String) {
            val req = StringRequest(
                Request.Method.GET,
                CHAT_ENDPOINT.buildUpon().appendQueryParameter("chat", chatId).build().toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<Message>::class.java)
                        .toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }

        fun sendStandEventMessage(
            mutableMedia: MutableLiveData<String>,
            chatId: String,
            message: String,
            sender: String,
        ) {
            val req = object : StringRequest(
                Method.POST, CHAT_SEND_PRIVATE_MESSAGE_ENDPOINT.toString(),
                { result ->
                    mutableMedia.postValue(result)
                },
                {

                }
            ) {
                override fun getParams(): MutableMap<String, String> {
                    val map = HashMap<String, String>()
                    map["chat"] = chatId
                    map["message"] = message
                    map["sender"] = sender
                    return map
                }
            }
            volley.add(req)
        }
    }
}