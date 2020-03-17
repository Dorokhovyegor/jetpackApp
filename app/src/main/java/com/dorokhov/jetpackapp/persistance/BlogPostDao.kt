package com.dorokhov.jetpackapp.persistance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dorokhov.jetpackapp.models.BlogPost

@Dao
interface BlogPostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(blogPost: BlogPost): Long

    @Query("SELECT * FROM blog_post")
    fun getAllBlogPosts(): LiveData<List<BlogPost>>
}