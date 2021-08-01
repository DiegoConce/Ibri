package com.ibri.utils

import android.content.SharedPreferences
import android.net.Uri

val BASE_URL = "http://192.168.1.13:8080"
val LOG_TEST = "TEST"

val GET_MEDIA_ENDPOINT: Uri =
    Uri.parse("${BASE_URL}/m/get")

val UPLOAD_MEDIA_ENDPOINT: Uri =
    Uri.parse("${BASE_URL}/m/save")

val MEDIA_UPDATE_ENDPOINT: Uri =
    Uri.parse("${BASE_URL}/user/m/set")
