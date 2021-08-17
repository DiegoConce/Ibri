package com.ibri.model.messaging

import android.os.Parcelable
import com.ibri.model.Company
import com.ibri.model.User
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Message(
    var id: String,
    var message: String,
    var privateSender: User?,
    var commercialSender: Company?,
    var created: Date
) : Parcelable {
}