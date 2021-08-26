package com.ibri.model.events

import android.os.Parcelable
import com.ibri.model.Company
import com.ibri.model.Media
import com.ibri.model.Tag
import com.ibri.model.User
import com.ibri.model.messaging.Room
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class CommercialEvent(
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
    override var city: String,
    override var tags: ArrayList<Tag>?,
    override var createdDate: Date,
    override var subscribers: ArrayList<User>?,
    override var deleted: Boolean,

    var rooms: ArrayList<Room>,
    var creator: Company,
    var maxRooms: Int
) : Event, Parcelable