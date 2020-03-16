package com.dorokhov.jetpackapp.repository.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.dorokhov.jetpackapp.api.main.OpenApiMainService
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.repository.NetworkBoundResource
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.account.state.AccountViewState
import com.dorokhov.jetpackapp.util.ApiSuccessResponse
import com.dorokhov.jetpackapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    val TAG = this.javaClass.canonicalName
    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true
            ) {

            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPk(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties?) {
                cacheObject?.let {
                    accountPropertiesDao.updateAccountProperties(
                        cacheObject.pk,
                        cacheObject.email,
                        cacheObject.username
                    )
                }
            }

            override suspend fun createCasheRequestAndReturn() {
                withContext(Main) {
                    // finish by viewing the db cache
                    result.addSource(loadFromCache()) { viewState ->
                        DataState.data(
                            data = viewState,
                            response = null
                        )
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(response.body)
                createCasheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties(
                    "Token ${authToken.token}"
                )
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.asLiveData()
    }

    fun cancelActiveJobs() {
        println("$TAG: ${"Cancel job"}")
    }

}


