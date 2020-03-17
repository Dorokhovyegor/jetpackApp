package com.dorokhov.jetpackapp.ui.main.blog.viewmodel

import com.dorokhov.jetpackapp.models.BlogPost

fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentNewStateOrNew()
    /* if (query.equals(update.blogFields.searchQuery)) { // тоже самое нам не интеерсно
         return
     }*/
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogListData(blogPost: List<BlogPost>) {
    val update = getCurrentNewStateOrNew()
    update.blogFields.blogList =
        blogPost // я не буду проверять на эквивалентность, за меня это потом сделает dif utils
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentNewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.blogFields. isQueryInProgress = isInProgress
    setViewState(update)
}

fun BlogViewModel.setIsAuthorOfBLogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentNewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}