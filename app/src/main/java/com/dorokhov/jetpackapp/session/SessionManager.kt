package com.dorokhov.jetpackapp.session

import android.app.Application
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

}