package com.dorokhov.jetpackapp.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.dorokhov.jetpackapp.models.BlogPost
import com.dorokhov.jetpackapp.repository.main.BlogRepository
import com.dorokhov.jetpackapp.session.SessionManager
import com.dorokhov.jetpackapp.ui.BaseViewModel
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogStateEvent
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogViewState
import com.dorokhov.jetpackapp.util.AbsentLiveData
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
    private val requestManager: RequestManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    override fun handleStateEvent(it: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        when (it) {
            is BlogStateEvent.BlogSearchEvent -> {
                return sessionManager.cashedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        viewState.value!!.blogFields.searchQuery
                    )
                } ?: return AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                return AbsentLiveData.create()
            }
            is BlogStateEvent.None -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setQuery(query: String) {
        val update = getCurrentNewStateOrNew()
        /* if (query.equals(update.blogFields.searchQuery)) { // тоже самое нам не интеерсно
             return
         }*/
        update.blogFields.searchQuery = query
        _viewState.value = update
    }

    fun setBlogListData(blogPost: List<BlogPost>) {
        val update = getCurrentNewStateOrNew()
        update.blogFields.blogList =
            blogPost // я не буду проверять на эквивалентность, за меня это потом сделает dif utils
        _viewState.value = update
    }

    fun setBlogPost(blogPost: BlogPost) {
        val update = getCurrentNewStateOrNew()
        update.viewBlogFields.blogPost = blogPost
        _viewState.value = update
    }

    fun setIsAuthorOfBLogPost(isAuthorOfBlogPost: Boolean) {
        val update = getCurrentNewStateOrNew()
        update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
        _viewState.value = update
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStateEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}