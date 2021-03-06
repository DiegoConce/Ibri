package com.ibri.model.messaging

import android.os.Parcelable
import com.ibri.model.Company
import com.ibri.model.Media
import com.ibri.model.User
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Room(
    var id: String,
    var name: String,
    var description: String,
    var maxMembers: Int,
    var membersNum: Int?,
    var members: ArrayList<User>,
    var host: Company,
    var created: Date,
    var creator: User?,
    var chat: Chat,
    var image: Media?,
    var deleted: Boolean
) : Parcelable