package com.dicoding.myappstories.db.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dicoding.myappstories.db.model.AddStoriesResponse
import com.dicoding.myappstories.db.model.GetStoriesResponse
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.db.remote.StoryRemoteMediator
import com.dicoding.myappstories.db.stories.StoriesAppDatabase
import com.dicoding.myappstories.db.stories.StoriesService
import com.dicoding.myappstories.utilities.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@ExperimentalPagingApi
class StoriesRepository @Inject constructor(
    private val storyDatabase: StoriesAppDatabase,
    private val storiesService: StoriesService,
) {

    private fun initializeBearerToken(token: String): String {
        return "Bearer $token"
    }

    suspend fun addNewStories(
        token: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
    ): Flow<Result<AddStoriesResponse>> = flow {
        try {
            val bearerToken = initializeBearerToken(token)
            val storyResponse =
                storiesService.addNewStories(bearerToken, file, description, lat, lon)
            emit(Result.success(storyResponse))
        } catch (story: Exception) {
            story.printStackTrace()
            emit(Result.failure(story))
        }
    }

    fun getAllStories(token: String): Flow<PagingData<Stories>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
            ),
            remoteMediator = StoryRemoteMediator(
                storyDatabase,
                storiesService,
                initializeBearerToken(token)
            ),
            pagingSourceFactory = {
                storyDatabase.getstoriesDao().getAllStories()
            }
        ).flow
    }

    fun getStoriesLocation(token: String): Flow<Result<GetStoriesResponse>> = flow {
        wrapEspressoIdlingResource {
            try {
                val bearerToken = initializeBearerToken(token)
                val storyResponse =
                    storiesService.getAllStories(bearerToken, size = 30, location = 1)
                emit(Result.success(storyResponse))
            } catch (story: Exception) {
                story.printStackTrace()
                emit(Result.failure(story))
            }
        }
    }
}