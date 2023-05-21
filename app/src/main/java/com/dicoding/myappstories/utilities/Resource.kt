package com.dicoding.myappstories.utilities

import androidx.test.espresso.idling.CountingIdlingResource

object Resource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    Resource.increment()
    return try {
        function()
    } finally {
        Resource.decrement()
    }
}