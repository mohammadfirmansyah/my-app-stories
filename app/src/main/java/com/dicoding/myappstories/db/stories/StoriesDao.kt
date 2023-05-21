package com.dicoding.myappstories.db.stories

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.myappstories.db.model.Stories

@Dao
interface StoriesDao {

    @Query("SELECT * FROM tbl_story")
    fun getAllStories(): PagingSource<Int, Stories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(vararg stories: Stories)

    @Query("DELETE FROM tbl_story")
    suspend fun deleteAllStories()

}