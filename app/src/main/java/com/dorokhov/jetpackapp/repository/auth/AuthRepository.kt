package com.dorokhov.jetpackapp.repository.auth

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.api.auth.OpenApiAuthService
import com.dorokhov.jetpackapp.api.auth.network_reponses.LoginResponse
import com.dorokhov.jetpackapp.api.auth.network_reponses.RegistrationResponse
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import com.dorokhov.jetpackapp.repository.NetworkBoundResource
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.Response
import com.dorokhov.jetpackapp.ui.ResponseType
import com.dorokhov.jetpackapp.ui.auth.state.AuthViewState
import com.dorokhov.jetpackapp.ui.auth.state.LoginFields
import com.dorokhov.jetpackapp.ui.auth.state.RegistrationFields
import com.dorokhov.jetpackapp.util.ApiSuccessResponse
import com.dorokhov.jetpackapp.util.ErrorHandling
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.dorokhov.jetpackapp.util.GenericApiResponse
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {

    val TAG = this.javaClass.canonicalName


    private var repositoryJob: Job? = null

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        var loginError = LoginFields(email, password).isValidForLogin()

        // something wrong
        if (!loginError.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                // incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )

            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()

    }

    private fun returnErrorResponse(
        loginFieldError: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(
                        loginFieldError,
                        responseType
                    )
                )
            }
        }
    }

    fun cancelActiveJob() {
        println("$TAG: Cancelling on-going job")
        repositoryJob?.cancel()
    }


    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirm_password: String
    ): LiveData<DataState<AuthViewState>> {

        // смотрим, есть ли ошибки в полях
        var registrationFieldsError =
            RegistrationFields(email, username, password, confirm_password).isValidForRegistration()

        if (!registrationFieldsError.equals(RegistrationFields.RegistrationError.none())) {
            return  returnErrorResponse(registrationFieldsError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                println("$TAG: Registration Response: ${response}")
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirm_password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()

    }
}