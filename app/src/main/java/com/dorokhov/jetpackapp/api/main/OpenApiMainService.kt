package com.dorokhov.jetpackapp.api.main

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.util.GenericApiResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>
}