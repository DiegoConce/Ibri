package com.ibri.ui.adapters

import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent

interface EventsOnClickListener {
    fun onItemSelected(item: StandardEvent)
    fun onItemSelected(item: CommercialEvent)
    fun onCreatorSelected(userId: String)
}