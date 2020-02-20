package com.dorokhov.jetpackapp.session

import android.app.Application
import com.dorokhov.jetpackapp.persistance.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

}