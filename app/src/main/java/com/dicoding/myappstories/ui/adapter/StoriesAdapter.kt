package com.dicoding.myappstories.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.myappstories.databinding.ItemStoriesRowBinding
import com.dicoding.myappstories.db.model.Stories
import com.dicoding.myappstories.ui.activity.DetailStoriesActivity
import com.dicoding.myappstories.ui.activity.DetailStoriesActivity.Companion.EXTRA_STORY
import com.dicoding.myappstories.utilities.setImageFromUrl
import com.dicoding.myappstories.utilities.setLocalDateFormat

class StoriesAdapter : PagingDataAdapter<Stories, StoriesAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemStoriesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, stories: Stories) {
            binding.apply {
                tvStoriesUsername.text = stories.name
                ivStoriesImage.setImageFromUrl(context, stories.photoUrl)
                tvStoriesDate.setLocalDateFormat(stories.createdAt)

                root.setOnClickListener {
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            root.context as Activity,
                            Pair(ivStoriesImage, "image_story"),
                            Pair(tvStoriesUsername, "image_username"),
                            Pair(tvStoriesDate, "image_date"),
                        )

                    Intent(context, DetailStoriesActivity::class.java).also { intent ->
                        intent.putExtra(EXTRA_STORY, stories)
                        context.startActivity(intent, optionsCompat.toBundle())
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStoriesRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }
        }
    }

}