package com.dicoding.myappstories.db.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: Users? = null,
    @field:SerializedName("error")
    val error: Boolean? = null,
    @field:SerializedName("message")
    val message: String? = null,

    )