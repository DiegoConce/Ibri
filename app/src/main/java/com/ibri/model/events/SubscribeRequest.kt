package com.ibri.model.events

import com.ibri.model.User


data class SubscribeRequest(
    var id: String,
    var accepted: Boolean,
    var user: User,
    var privateEvent: StandardEvent
)