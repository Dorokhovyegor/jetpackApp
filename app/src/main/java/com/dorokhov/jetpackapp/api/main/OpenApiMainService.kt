package com.dorokhov.jetpackapp.api.main

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.api.GenericResponse
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.util.GenericApiResponse
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun saveAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun updatePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") currantPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirmNewPassword: String
    ):  LiveData<GenericApiResponse<GenericResponse>>
}
