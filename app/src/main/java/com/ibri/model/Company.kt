package com.ibri.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Company(
    var id: String,
    var name: String,
    var email: String,
    var password: String,
    var avatar: Media?,
    var pIva: String,
    var bio: String,
    var deleted: Boolean,
    var numOfEvents: Int
) : Parcelable