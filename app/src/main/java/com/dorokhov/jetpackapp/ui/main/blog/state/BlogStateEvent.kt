package com.dorokhov.jetpackapp.ui.main.blog.state

sealed class BlogStateEvent {
    class BlogSearchEvent : BlogStateEvent()

    class CheckAuthorOfBlogPost: BlogStateEvent()

    class DeleteBlogPostEvent: BlogStateEvent()

    class None: BlogStateEvent()
}