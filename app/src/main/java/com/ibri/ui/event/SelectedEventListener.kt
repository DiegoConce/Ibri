package com.ibri.ui.event

import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent

interface SelectedEventListener {
    fun onItemSelected(item: StandardEvent)
    fun onItemSelected(item: CommercialEvent)
    fun onCreatorSelected(userId: String)
}