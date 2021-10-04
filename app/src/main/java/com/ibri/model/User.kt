package com.ibri.model

import android.os.Parcelable
import com.ibri.model.enum.Gender
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class User(
    var id: String,
    var name: String,
    var surname: String,
    var email: String,
    var password: String,
    var avatar: Media?,
    var bio: String?,
    var friends: Int,
    var numOfEvents: Int,
    var birthday: Date,
    var gender: Gender,
    var deleted: Boolean,
) : Parcelable