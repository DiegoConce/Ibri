package com.ibri.model

import com.ibri.model.enum.Gender
import java.util.*

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
    var deleted: Boolean
) {
}