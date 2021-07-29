package com.ibri.repository


import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.events.StandardEvent
import com.ibri.utils.BASE_URL

class StandardEventRepository {

    companion object {
        private val ALL_STANDARD_EVENTS_ENDPOINT = Uri.parse("${BASE_URL}/event/private/all")
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

        fun closeVolley() {
            volley.stop()
        }
    }

}