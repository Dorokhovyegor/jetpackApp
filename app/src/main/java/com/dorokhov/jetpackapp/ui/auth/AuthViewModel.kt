package com.dorokhov.jetpackapp.ui.auth

import androidx.lifecycle.ViewModel
import com.dorokhov.jetpackapp.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}