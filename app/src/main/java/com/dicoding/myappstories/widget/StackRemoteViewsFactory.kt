package com.dicoding.myappstories.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.room.Room
import com.dicoding.myappstories.R
import com.dicoding.myappstories.db.stories.StoriesAppDatabase
import com.dicoding.myappstories.db.stories.StoriesEntity
import com.dicoding.myappstories.utilities.ConstValue.DB_NAME
import com.dicoding.myappstories.utilities.urlToBitmap

internal class StackRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var stories: MutableList<StoriesEntity> = mutableListOf()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val database = Room.databaseBuilder(
            context.applicationContext, StoriesAppDatabase::class.java,
            DB_NAME
        ).build()
        database.getStoriesDaoWidget().getAllStoriesWidget().forEach {
            stories.add(
                StoriesEntity(
                    it.id,
                    it.photoUrl
                )
            )
        }
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.stories_widget_item)
        rv.setImageViewBitmap(R.id.imgStories, urlToBitmap(stories[position].photoUrl))

        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}