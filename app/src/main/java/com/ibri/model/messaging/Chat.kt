package com.ibri.model.messaging

import java.util.*
import kotlin.collections.ArrayList

data class Chat(
    var id:String,
    var messages: ArrayList<Message>,
    var created: Date,
    var deleted: Boolean
) {
}