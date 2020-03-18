package com.dorokhov.jetpackapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.dorokhov.jetpackapp.api.GenericResponse
import com.dorokhov.jetpackapp.api.main.OpenApiMainService
import com.dorokhov.jetpackapp.api.main.network_responses.BlogListSearchResponse
import com.dorokhov.jetpackapp.models.AuthToken
import com.dorokhov.jetpackapp.models.BlogPost
import com.dorokhov.jetpackapp.persistance.BlogPostDao
import com.dorokhov.jetpackapp.persistance.returnOrderedBlogQuery
import com.dorokhov.jetpackapp.repository.JobManager
import com.dorokhov.jetpackapp.repository.NetworkBoundResource
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogViewState
import com.dorokhov.jetpackapp.util.*
import com.dorokhov.jetpackapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.dorokhov.jetpackapp.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {
            override suspend fun createCasheRequestAndReturn() {
                withContext(Main) {
                    // finish by viewing db cache
                    result.addSource(loadFromCache()) { viewState ->
                        viewState.blogFields.isQueryInProgress = false
                        if (page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(
                            DataState.data(
                                viewState, null
                            )
                        )
                    }
                }

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for (blogPost in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                            pk = blogPost.pk,
                            title = blogPost.title,
                            body = blogPost.body,
                            date_updated = DateUtils.convertServerStringDateToLong(blogPost.date_updated),
                            image = blogPost.image,
                            username = blogPost.username,
                            slug = blogPost.slug
                        )
                    )
                }
                updateLocalDb(blogPostList)
                createCasheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query,
                    filterAndOrder,
                    page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(
                    query,
                    filterAndOrder,
                    page
                )
                    .switchMap {
                        object : LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if (cacheObject != null) {
                    withContext(IO) {
                        for (blogPost in cacheObject) {
                            try {
                                // launch each insert as a separate  job to executed in parallel
                                launch {
                                    println("$TAG: update local db: inserting blog ${blogPost}")
                                    blogPostDao.insert(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "updateLocalDb error on blog with slug ${blogPost.slug}")
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }
        }.asLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            // not applicable
            override suspend fun createCasheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    var isAuthor = false
                    if (response.body.response.equals(SuccessHandling.RESPONSE_HAS_PERMISSION_TO_EDIT)) {
                        isAuthor = true
                    }

                    onCompleteJob(DataState.data(
                        data = BlogViewState(
                            viewBlogFields = BlogViewState.ViewBlogFields(
                                isAuthorOfBlogPost = isAuthor
                            )
                        ),
                        response = null
                    ))
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token}",
                    slug
                )
            }

            // not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }
        }.asLiveData()
    }
}




