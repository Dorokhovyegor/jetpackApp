package com.dorokhov.jetpackapp.di.auth

import androidx.lifecycle.ViewModel
import com.dorokhov.jetpackapp.di.ViewModelKey
import com.dorokhov.jetpackapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}

