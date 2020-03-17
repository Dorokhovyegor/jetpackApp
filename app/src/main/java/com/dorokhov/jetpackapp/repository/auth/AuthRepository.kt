package com.dorokhov.jetpackapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.api.auth.OpenApiAuthService
import com.dorokhov.jetpackapp.api.auth.network_reponses.LoginResponse
import com.dorokhov.jetpackapp.api.auth.network_reponses.RegistrationResponse
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.persistance.AuthTokenDao
import com.dorokhov.jetpackapp.repository.JobManager
import com.dorokhov.jetpackapp.repository.NetworkBoundResource
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.Response
import com.dorokhov.jetpackapp.ui.ResponseType
import com.dorokhov.jetpackapp.ui.auth.state.AuthViewState
import com.dorokhov.jetpackapp.ui.auth.state.LoginFields
import com.dorokhov.jetpackapp.ui.auth.state.RegistrationFields
import com.dorokhov.jetpackapp.util.*
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.dorokhov.jetpackapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPrefs: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) : JobManager("AuthRepository") {

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        var loginError = LoginFields(email, password).isValidForLogin()

        // something wrong
        if (!loginError.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,// для входа действительна нужна сеть
            true,
            false

        ) {

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCasheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                // incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(ErrorHandling.GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                // don't care about result. Just insert if it doesn't exist b/c foreign key relationship
                // with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email)

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
                addJob("attemptLogin", job)
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
            return returnErrorResponse(registrationFieldsError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCasheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {
                println("$TAG: Registration Response: ${response}")
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                // don't care about result. Just insert if it doesn't exist b/c foreign key relationship
                // with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )

                // will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )

                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }

                saveAuthenticatedUserToPrefs(email)

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
                addJob("attemptRegistration", job)
            }
        }.asLiveData()

    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUserEmail: String? =
            sharedPrefs.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUserEmail.isNullOrBlank()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found...")
            return returnNoTokenFound()
        }

        return object : NetworkBoundResource<Void, Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
            false,
            false
        ) {

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override suspend fun createCasheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let { accountProperties ->
                    Log.d(TAG, "checkPreviousAuthUser: search for token: $accountProperties")

                    accountProperties?.let {
                        if (it.pk > -1) {
                            authTokenDao.searchByPk(it.pk)?.let { authToken ->
                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }

                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

            // not used in this case
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {

            }

            // not used in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override fun setJob(job: Job) {
                addJob("checkPreviousAuthUser", job)
            }
        }.asLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    data = null,
                    response = Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None())
                )
            }
        }
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }
}