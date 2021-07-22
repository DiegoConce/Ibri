package com.ibri.model.events

import com.ibri.model.Media
import com.ibri.model.Tag
import com.ibri.model.User
import com.ibri.model.messaging.Chat
import java.util.*

data class StandardEvent(
    override var id: String,
    override var title: String,
    override var description: String,
    override var media: Media?,
    override var startSubscription: Date,
    override var eventDay: Date,
    override var hour: Int,
    override var minute: Int,
    override var guests: Int,
    override var maxGuests: Int,
    override var minGuests: Int,
    override var promoted: Boolean,
    override var views: Int,
    override var lat: String,
    override var lon: String,
    override var address: String,
    override var tags: ArrayList<Tag>?,
    override var createdDate: Date,
    override var subscribers: ArrayList<User>?,
    override var deleted: Boolean,

    var creator: User,
    var chat: Chat?,
    var isPrivate:Boolean
) : Event {
}