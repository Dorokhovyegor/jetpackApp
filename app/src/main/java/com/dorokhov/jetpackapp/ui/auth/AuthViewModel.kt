package com.dorokhov.jetpackapp.ui.auth

import androidx.lifecycle.ViewModel
import com.dorokhov.jetpackapp.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {

}