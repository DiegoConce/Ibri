package com.ibri.model

import com.ibri.model.events.StandardEvent
import java.util.*

data class Question(
    var id: String,
    var question: String,
    var answer: String?,
    var responseDate: Date?,
    var privateEvent: StandardEvent,
)