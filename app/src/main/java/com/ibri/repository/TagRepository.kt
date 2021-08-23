package com.ibri.repository

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.ibri.MainApplication
import com.ibri.model.Tag
import com.ibri.utils.BASE_URL

class TagRepository {
    companion object {
        private val TAGS_ENDPOINT = Uri.parse("${BASE_URL}/tags/all")
        private val volley = Volley.newRequestQueue(MainApplication.applicationContext())

        fun getAllTags(mutableMediaList: MutableLiveData<ArrayList<Tag>>) {
            val req = StringRequest(
                Request.Method.GET, TAGS_ENDPOINT.toString(),
                { result ->
                    val a = Gson().fromJson(result, Array<Tag>::class.java).toCollection(ArrayList())
                    mutableMediaList.postValue(a)
                },
                {

                }
            )
            volley.add(req)
        }
    }
}