package com.dicoding.myappstories.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.myappstories.db.model.LoginResponse
import com.dicoding.myappstories.db.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class LoginPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun loginUser(email: String, password: String): Flow<Result<LoginResponse>> =
        authRepository.loginUser(email, password)
}