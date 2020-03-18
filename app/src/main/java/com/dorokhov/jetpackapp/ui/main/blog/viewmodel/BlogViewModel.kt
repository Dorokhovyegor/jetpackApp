package com.dorokhov.jetpackapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.dorokhov.jetpackapp.persistance.BlogQueryUtils
import com.dorokhov.jetpackapp.persistance.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.dorokhov.jetpackapp.repository.main.BlogRepository
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.BaseViewModel
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogStateEvent
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogViewState
import com.dorokhov.jetpackapp.util.AbsentLiveData
import com.dorokhov.jetpackapp.util.PreferenceKeys.Companion.BLOG_FILTER
import com.dorokhov.jetpackapp.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject


/**
 * https://www.youtube.com/watch?v=MAlSjtxy5ak лол
 * */

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPrefs: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        setBlogFilter(sharedPrefs.getString(BLOG_FILTER, BlogQueryUtils.BLOG_FILTER_DATE_UPDATED))
        setBlogOrder(sharedPrefs.getString(BLOG_ORDER, BLOG_ORDER_ASC)!!)
    }



    override fun handleStateEvent(it: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (it) {
            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: return AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
               return sessionManager.cashedToken.value?.let { authToken ->
                   blogRepository.isAuthorOfBlogPost(
                       authToken = authToken,
                       slug = getSlug()
                   )
               } ?: return AbsentLiveData.create()
            }
            is BlogStateEvent.DeleteBlogPostEvent -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken = authToken,
                        blogPost = getBlogPost()
                    )
                } ?: return AbsentLiveData.create()
            }
            is BlogStateEvent.None -> {
                return object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.data(null, null)
                    }
                }
            }
        }
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(BLOG_FILTER, filter)
        editor.apply()

        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(BlogStateEvent.None())
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}