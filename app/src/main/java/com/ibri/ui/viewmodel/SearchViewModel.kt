package com.ibri.ui.viewmodel

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ibri.model.Tag
import com.ibri.repository.TagRepository

class SearchViewModel : ViewModel() {
    val tagList = MutableLiveData<ArrayList<Tag>>()
    val selectedTag = MutableLiveData<Tag>()
    var isStandardEvent = MutableLiveData(false)
    var distanceInM = MutableLiveData(0)
    var location = MutableLiveData<Location>()

    fun getTags() = TagRepository.getAllTags(tagList)
}