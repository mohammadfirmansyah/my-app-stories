package com.dicoding.myappstories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.dicoding.myappstories.db.model.GetStoriesResponse
import com.dicoding.myappstories.db.repository.StoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class MapsPageViewModel @Inject constructor(private val storiesRepository: StoriesRepository) :
    ViewModel() {

    fun getStoriesLocation(token: String): Flow<Result<GetStoriesResponse>> =
        storiesRepository.getStoriesLocation(token)

}