package com.ibri.model.messaging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Chat(
    var id: String,
    var messages: ArrayList<Message>,
    var created: Date,
    var deleted: Boolean
) : Parcelable