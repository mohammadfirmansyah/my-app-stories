package com.dicoding.myappstories.db.stories

import androidx.room.Dao
import androidx.room.Query
import com.dicoding.myappstories.db.model.Stories

@Dao
interface StoriesWidgetDao {

    @Query("SELECT * FROM tbl_story")
    fun getAllStoriesWidget(): List<Stories>

}