package com.dorokhov.jetpackapp.ui

import com.dorokhov.jetpackapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    protected var TAG = "AppDebug"

    @Inject
    lateinit var sessionManager: SessionManager

}