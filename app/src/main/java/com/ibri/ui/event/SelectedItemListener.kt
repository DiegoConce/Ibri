package com.ibri.ui.event

import com.ibri.model.events.CommercialEvent
import com.ibri.model.events.StandardEvent

interface SelectedItemListener {
    fun onItemSelected(item: StandardEvent)
    fun onCreatorSelected(userId: String)
    fun onItemSelected(item: CommercialEvent)
}