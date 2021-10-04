package com.ibri.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Achievement(
    var id: String,
    var title: String,
    var description: String,
    var image: Media?,
    var unlocked: Boolean
) : Parcelable