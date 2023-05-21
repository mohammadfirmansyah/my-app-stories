package com.dicoding.myappstories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import com.dicoding.myappstories.db.model.AddStoriesResponse
import com.dicoding.myappstories.db.repository.StoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class AddStoriesViewModel @Inject constructor(
    private val storiesRepository: StoriesRepository,
) : ViewModel() {

    suspend fun uploadImage(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?,
    ): Flow<Result<AddStoriesResponse>> =
        storiesRepository.addNewStories(token, file, description, lat, lon)

}