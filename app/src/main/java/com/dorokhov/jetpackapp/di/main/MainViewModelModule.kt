package com.dorokhov.jetpackapp.di.main

import androidx.lifecycle.ViewModel
import com.dorokhov.jetpackapp.di.ViewModelKey
import com.dorokhov.jetpackapp.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AccountViewModel): ViewModel

}