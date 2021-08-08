package com.ibri.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    var id: Long,
    var name: String,
) : Parcelable