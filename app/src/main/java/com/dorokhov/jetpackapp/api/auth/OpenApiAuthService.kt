package com.dorokhov.jetpackapp.api.auth

import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.api.auth.network_reponses.LoginResponse
import com.dorokhov.jetpackapp.api.auth.network_reponses.RegistrationResponse
import com.dorokhov.jetpackapp.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface OpenApiAuthService {

    @POST("account/login") // todo input url
    @FormUrlEncoded
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>


}