package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.events.CommercialEvent
import com.ibri.utils.BASE_URL

class CommercialEventRepository {

    companion object {
        private val COMMERCIAL_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/commercial/all")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())


        fun getCommercialEvent(mutableMediaList: MutableLiveData<ArrayList<CommercialEvent>>) {
            val req = StringRequest(
                Request.Method.GET, COMMERCIAL_EVENTS_ENDPOINT.toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<CommercialEvent>::class.java).toCollection(ArrayList())
                    mutableMediaList.postValue(a)
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