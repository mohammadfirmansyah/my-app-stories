package com.dicoding.myappstories.db.auth

import com.dicoding.myappstories.db.model.LoginResponse
import com.dicoding.myappstories.db.model.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {

    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

}