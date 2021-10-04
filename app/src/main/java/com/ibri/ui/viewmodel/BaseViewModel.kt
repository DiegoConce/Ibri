package com.ibri.ui.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.VolleyError

open class BaseViewModel : ViewModel() {
    val errorResponse = MutableLiveData<String>()

    protected open fun funErrorGeneric(throwable: VolleyError){
        handleThrowable(throwable)
    }

    private fun handleThrowable(throwable: VolleyError) {
        errorResponse.value = throwable.toString()

        Log.v(ContentValues.TAG,"VolleyError: ${throwable}")
        Log.v(ContentValues.TAG,"VolleyError message: ${throwable.message}")
        Log.v(ContentValues.TAG,"VolleyError stackTrace: ${throwable.printStackTrace()}")
    }
}