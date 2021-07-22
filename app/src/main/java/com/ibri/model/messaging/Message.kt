package com.ibri.model.messaging

import com.ibri.model.Company
import com.ibri.model.User
import java.util.*

data class Message(
    var id: String,
    var message: String,
    var privateSender: User?,
    var commercialSender: Company?,
    var created: Date
) {
}