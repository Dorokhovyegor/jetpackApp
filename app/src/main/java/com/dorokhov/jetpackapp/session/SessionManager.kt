package com.dorokhov.jetpackapp.session

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.network.NetworkStateHolder
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
) {

    private val TAG = "AppDebug"

    private val _cashedToken = MutableLiveData<AuthToken>()

    val cashedToken: LiveData<AuthToken>
        get() = _cashedToken

    fun login(newValue: AuthToken) {
        setValue(newValue)
    }

    fun logout() {
        Log.d(TAG, "logout...")

        GlobalScope.launch(IO) {
            var errorMessage: String? = null
            try {
                cashedToken.value!!.account_pk?.let {
                    authTokenDao.nullifyToken(it)
                }
            } catch (e: CancellationException) {
                Log.e(TAG, "logout ${e.message}")
                errorMessage = e.message
            } catch (e: Exception) {
                Log.e(TAG, "logout ${e.message}")
                errorMessage = e.message + "\n" + e.message
            } finally {
                errorMessage?.let {
                    Log.e(TAG, "logout: ${errorMessage}")
                }
                Log.d(TAG, "logout: finally...")
                setValue(null)
            }
        }
    }


    fun setValue(newValue: AuthToken?) {
        GlobalScope.launch(Main) {
            if (_cashedToken.value != newValue) {
                _cashedToken.value = newValue
            }
        }
    }

    fun isConnected(): Boolean {
        return NetworkStateHolder.isConnected
    }

    // todo how to translate it from async to sync?
    fun isConnectedToTheInternet(): Boolean{
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        try{
            return cm.activeNetworkInfo.isConnected
        }catch (e: Exception){
            Log.e(TAG, "isConnectedToTheInternet: ${e.message}")
        }
        return false
    }
}