package com.dorokhov.jetpackapp.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.models.BlogPost

@Database(entities = [AuthToken::class, AccountProperties::class, BlogPost::class], version = 2)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    abstract fun getBlogPostDao(): BlogPostDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}