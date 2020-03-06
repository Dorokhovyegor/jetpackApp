package com.dorokhov.jetpackapp.di.main

import com.dorokhov.jetpackapp.ui.main.account.AccountFragment
import com.dorokhov.jetpackapp.ui.main.account.ChangePasswordFragment
import com.dorokhov.jetpackapp.ui.main.account.UpdateAccountFragment
import com.dorokhov.jetpackapp.ui.main.blog.BlogFragment
import com.dorokhov.jetpackapp.ui.main.blog.UpdateBlogFragment
import com.dorokhov.jetpackapp.ui.main.blog.ViewBlogFragment
import com.dorokhov.jetpackapp.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}