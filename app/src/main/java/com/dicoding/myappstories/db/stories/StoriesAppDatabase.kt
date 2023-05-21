package com.dicoding.myappstories.db.stories

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.db.remote.RemoteKeys
import com.dicoding.myappstories.db.remote.RemoteKeysDao

@Database(
    entities = [Stories::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoriesAppDatabase : RoomDatabase() {
    abstract fun getstoriesDao(): StoriesDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun getStoriesDaoWidget(): StoriesWidgetDao
}