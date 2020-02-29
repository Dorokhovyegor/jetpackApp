package com.dorokhov.jetpackapp.repository.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.dorokhov.jetpackapp.api.auth.OpenApiAuthService
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.Response
import com.dorokhov.jetpackapp.ui.ResponseType
import com.dorokhov.jetpackapp.ui.auth.state.AuthViewState
import com.dorokhov.jetpackapp.util.ApiEmptyResponse
import com.dorokhov.jetpackapp.util.ApiErrorResponse
import com.dorokhov.jetpackapp.util.ApiSuccessResponse
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        // switch map просто преобразует данные находящиеся внутри liveData. внутри лямды уже имеем развернутые данные
        return openApiAuthService.login(email, password).switchMap { response ->
            object : LiveData<DataState<AuthViewState>>() {
                override fun onActive() {
                    super.onActive()
                    when (response) {
                        is ApiSuccessResponse -> {
                            value = DataState.data(
                                data = AuthViewState(
                                    authToken = AuthToken(
                                        response.body.pk,
                                        response.body.token
                                    )
                                ),
                                response = null
                            )
                        }
                        is ApiErrorResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = response.errorMessage,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                        is ApiEmptyResponse -> {
                            value = DataState.error(
                                response = Response(
                                    message = ERROR_UNKNOWN,
                                    responseType = ResponseType.Dialog()
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirm_password: String
    ): LiveData<DataState<AuthViewState>> {
        // switch map просто преобразует данные находящиеся внутри liveData. внутри лямды уже имеем развернутые данные
        return openApiAuthService.register(email, username, password, confirm_password).
            switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = response.errorMessage,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        message = ERROR_UNKNOWN,
                                        responseType = ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

}