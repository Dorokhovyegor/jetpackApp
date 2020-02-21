package com.dorokhov.jetpackapp.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dorokhov.jetpackapp.models.AccountProperties
import com.dorokhov.jetpackapp.models.AuthToken

@Database(entities = [AuthToken::class, AccountProperties::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getAuthTokenDao(): AuthTokenDao

    abstract fun getAccountProperties(): AccountPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }

}