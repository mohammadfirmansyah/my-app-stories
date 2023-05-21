package com.dicoding.myappstories.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.myappstories.db.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterPageViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    suspend fun registerUser(name: String, email: String, password: String) =
        authRepository.userRegister(name, email, password)

}