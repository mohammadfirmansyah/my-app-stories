package com.dicoding.myappstories.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.db.repository.StoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class HomepageViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository,
) : ViewModel() {

    fun getAllStories(token: String): LiveData<PagingData<Stories>> =
        storiesRepository.getAllStories(token).cachedIn(viewModelScope).asLiveData()

}