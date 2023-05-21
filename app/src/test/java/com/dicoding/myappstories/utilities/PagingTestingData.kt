package com.dicoding.myappstories.utilities

import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.myappstories.db.model.Stories

class PagingTestingData : PagingSource<Int, Stories>() {

    companion object {
        fun snap(items: List<Stories>): PagingData<Stories> {
            return PagingData.from(items)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stories> {
        val items = emptyList<Stories>()
        return LoadResult.Page(items, null, null)
    }

    override fun getRefreshKey(state: PagingState<Int, Stories>): Int? {
        return null
    }
}