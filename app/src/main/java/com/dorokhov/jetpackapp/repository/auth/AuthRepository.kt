package com.dorokhov.jetpackapp.repository.auth

import com.dorokhov.jetpackapp.api.auth.OpenApiAuthService
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import com.dorokhov.jetpackapp.session.SessionManager

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

}