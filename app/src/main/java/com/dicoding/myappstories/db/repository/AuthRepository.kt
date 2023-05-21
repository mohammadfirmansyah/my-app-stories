package com.dicoding.myappstories.db.repository

import com.dicoding.myappstories.db.auth.AuthService
import com.dicoding.myappstories.db.model.LoginResponse
import com.dicoding.myappstories.db.model.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
) {

    suspend fun userRegister(
        name: String,
        email: String,
        password: String,
    ): Flow<Result<RegisterResponse>> = flow {
        try {
            val response = authService.registerUser(name, email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val response = authService.loginUser(email, password)
            emit(Result.success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)

}