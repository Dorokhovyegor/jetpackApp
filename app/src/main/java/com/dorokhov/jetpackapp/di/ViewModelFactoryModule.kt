package com.dorokhov.jetpackapp.di

import androidx.lifecycle.ViewModelProvider
import com.dorokhov.jetpackapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}