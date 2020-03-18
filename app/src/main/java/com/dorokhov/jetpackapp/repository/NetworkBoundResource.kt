package com.dorokhov.jetpackapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.Response
import com.dorokhov.jetpackapp.ui.ResponseType
import com.dorokhov.jetpackapp.util.*
import com.dorokhov.jetpackapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.dorokhov.jetpackapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean, // is there a network connection?
    isNetworkRequest: Boolean, // is this a network request?
    shouldCancelIfNoInternet: Boolean, // should this job be cancelled if there is no network?
    shouldLoadFromCache: Boolean // should be cached data be loaded?
) {

    private val TAG = "AppDebug"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(true, cashedData = null))

        if (shouldLoadFromCache) {
            val dbSource = loadFromCache()
            result.addSource(dbSource) {
                result.removeSource(dbSource)
                setValue(DataState.loading(true, cashedData = it))
            }
        }

        if (isNetworkRequest) {
            if (isNetworkAvailable) {
                doNetworkRequest()
            } else {
                if (shouldCancelIfNoInternet) {
                    onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, true, false)
                } else {
                    doCacheRequest()
                }
            }
        } else { // если не требуется соединение с интернетом
            doCacheRequest()
        }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            // delay for test
            delay(Constants.TESTING_CACHE_DELAY)
            // View data from cache ONLY and return
            createCasheRequestAndReturn()
        }
    }

    private fun doNetworkRequest() {
        // корутины стартуют одновременно, если первый не успеет выполнится, второй отменяет запущенную работу, как таймаут
        coroutineScope.launch {
            // simulate delay
            delay(TESTING_NETWORK_DELAY)
            // switch context coroutine
            withContext(Main) {
                val apiResponse = createCall()
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)
                    coroutineScope.launch {
                        handleNetworkCall(response)
                    }
                }
            }
        }

        GlobalScope.launch(IO) {
            delay(Constants.NETWORK_TIMEOUT)
            if (!job.isCompleted) {
                Log.e(TAG, "NetworkBoundResource: Job network timeout")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                onErrorReturn("HTTP 204, Returned nothing.", true, false)
            }
        }
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onErrorReturn(errorMessage: String?, shouldUserDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUserDialog
        var responseType: ResponseType = ResponseType.None()
        if (msg == null) {
            msg = ErrorHandling.ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ErrorHandling.ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }

        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: Called...")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (job.isCancelled) {
                        Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ErrorHandling.ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: Job has been completed.")
                        // do nothing. Should be handled already
                    }
                }
            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCasheRequestAndReturn()

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract fun setJob(job: Job)



}