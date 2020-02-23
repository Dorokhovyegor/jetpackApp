package com.dorokhov.jetpackapp.di.auth

import com.dorokhov.jetpackapp.ui.auth.ForgotPasswordFragment
import com.dorokhov.jetpackapp.ui.auth.LauncherFragment
import com.dorokhov.jetpackapp.ui.auth.LoginFragment
import com.dorokhov.jetpackapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}