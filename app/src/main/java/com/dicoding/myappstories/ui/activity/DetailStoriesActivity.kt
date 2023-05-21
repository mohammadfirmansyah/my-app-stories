package com.dicoding.myappstories.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dicoding.myappstories.databinding.ActivityDetailStoriesBinding
import com.dicoding.myappstories.db.model.Stories
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportPostponeEnterTransition()

        val stories = intent.getParcelableExtra<Stories>(EXTRA_STORY)
        initStoryData(stories)

        initUI()
        initAction()
    }

    private fun initUI() {
        supportActionBar?.hide()
    }

    private fun initAction() {
        binding.apply {
            imgBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
                finish()
            }
        }
    }

    private fun initStoryData(stories: Stories?) {
        if (stories != null) {
            binding.apply {

                Glide
                    .with(this@DetailStoriesActivity)
                    .load(stories.photoUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean,
                        ): Boolean {
                            supportStartPostponedEnterTransition()
                            return false
                        }
                    })
                    .into(imgStoriesThumbnail)

                tvGreetingName.text = stories.name
                tvStoriesDesc.text = stories.description
            }
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }

}