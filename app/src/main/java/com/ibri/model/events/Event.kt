package com.ibri.model.events

import com.ibri.model.Media
import com.ibri.model.Tag
import com.ibri.model.User
import java.util.*

interface Event {
    var id: String
    var title: String
    var description: String
    var media: Media?
    var startSubscription: Date
    var eventDay: Date
    var hour: Int
    var minute: Int
    var guests: Int
    var maxGuests: Int
    var minGuests: Int
    var promoted: Boolean
    var views: Int
    var lat: String
    var lon: String
    var address: String
    var city: String
    var tags: ArrayList<Tag>?
    var createdDate: Date
    var subscribers: ArrayList<User>?
    var deleted: Boolean
}