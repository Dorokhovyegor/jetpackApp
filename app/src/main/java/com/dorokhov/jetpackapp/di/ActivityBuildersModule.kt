package com.dorokhov.jetpackapp.di

import com.dorokhov.jetpackapp.di.auth.AuthFragmentBuildersModule
import com.dorokhov.jetpackapp.di.auth.AuthModule
import com.dorokhov.jetpackapp.di.auth.AuthScope
import com.dorokhov.jetpackapp.di.auth.AuthViewModelModule
import com.dorokhov.jetpackapp.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

}