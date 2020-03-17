package com.dorokhov.jetpackapp.ui.auth

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.repository.auth.AuthRepository
import com.dorokhov.jetpackapp.ui.BaseViewModel
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.auth.state.AuthStateEvent
import com.dorokhov.jetpackapp.ui.auth.state.AuthViewState
import com.dorokhov.jetpackapp.ui.auth.state.LoginFields
import com.dorokhov.jetpackapp.ui.auth.state.RegistrationFields
import com.dorokhov.jetpackapp.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentNewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentNewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentNewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _viewState.value = update
    }

    override fun handleStateEvent(it: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (it) {
            // just return a place holder
            is AuthStateEvent.LoginAttemptEvent -> {
                return authRepository.attemptLogin(it.email, it.password)
            }

            // just return a place holder
            is AuthStateEvent.RegisterAttemptEvent -> {
                return authRepository.attemptRegistration(
                    it.email,
                    it.username,
                    it.password,
                    it.confirm_password
                )
            }

            // just return a place holder
            is AuthStateEvent.CheckPreviousAuthEvent -> {
                return authRepository.checkPreviousAuthUser()
            }

            is AuthStateEvent.None -> {
                return object: LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    private fun handlePendingData() {
        setStateEvent(AuthStateEvent.None())
    }

    fun cancelActiveJobs() {
        handlePendingData()
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}