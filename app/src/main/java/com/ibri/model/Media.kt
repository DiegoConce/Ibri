package com.ibri.model

import com.ibri.model.enum.MediaType

data class Media(
    var id: String,
    var url: String,
    var type: MediaType
)