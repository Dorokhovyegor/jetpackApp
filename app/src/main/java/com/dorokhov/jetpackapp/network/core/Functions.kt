package com.dorokhov.jetpackapp.network.core

import android.util.Log

internal inline fun <T> T.safeRun(TAG: String = "", block: T.() -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        //ignore but log it
        Log.e(TAG, e.toString())
    }
}