package com.dorokhov.jetpackapp.di.main

import com.dorokhov.jetpackapp.api.main.OpenApiMainService
import com.dorokhov.jetpackapp.persistance.AccountPropertiesDao
import com.dorokhov.jetpackapp.persistance.AppDatabase
import com.dorokhov.jetpackapp.persistance.BlogPostDao
import com.dorokhov.jetpackapp.repository.main.AccountRepository
import com.dorokhov.jetpackapp.repository.main.BlogRepository
import com.dorokhov.jetpackapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder.build().create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(
            openApiMainService,
            accountPropertiesDao,
            sessionManager
        )
    }

    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
    }


}