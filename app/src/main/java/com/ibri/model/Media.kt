package com.ibri.model

import android.os.Parcelable
import com.ibri.model.enum.MediaType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    var id: String,
    var url: String,
    var type: MediaType
) : Parcelable