package com.dicoding.myappstories.db.stories

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_story")
data class StoriesEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @ColumnInfo(name = "photo_url")
    val photoUrl: String,
)