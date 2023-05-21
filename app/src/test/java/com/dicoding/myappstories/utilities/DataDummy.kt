package com.dicoding.myappstories.utilities

import com.dicoding.myappstories.db.model.Stories

object DataDummy {
    fun generateDummyListStoriesResponse(): List<Stories> {
        val listStories = ArrayList<Stories>()

        for (i in 0..10) {
            val stories = Stories(
                id = "story-hI4oMRDhnzlK4WN1",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1683755925724_ffjsnc04.jpg",
                createdAt = "2023-05-10T21:58:45.725Z",
                name = "HelloWorld!",
                description = "tes7",
                lon = 112.5994899412002,
                lat = -7.963963963963964
            )
            listStories.add(stories)
        }
        return listStories
    }
}