package com.ibri.model.events

data class SubscribedEventResponse(
    var privateEvent: StandardEvent?,
    var commercialEvent: CommercialEvent?
)