package com.dorokhov.jetpackapp.ui.main.blog.viewmodel

import com.dorokhov.jetpackapp.models.BlogPost

fun BlogViewModel.getSlug(): String {
    getCurrentNewStateOrNew().let {
        it.viewBlogFields.blogPost?.let {
            return it.slug
        }
    }
    return ""
}

fun BlogViewModel.isAuthorOfBlogPost(): Boolean {
    getCurrentNewStateOrNew().let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}

fun BlogViewModel.getFilter(): String {
    getCurrentNewStateOrNew().let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getOrder(): String {
    getCurrentNewStateOrNew().let {
        return it.blogFields.order
    }
}

fun BlogViewModel.getSearchQuery(): String {
    getCurrentNewStateOrNew().let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getPage(): Int {
    getCurrentNewStateOrNew().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getIsQueryExhausted(): Boolean {
    getCurrentNewStateOrNew().let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getIsQueryInProgress(): Boolean {
    getCurrentNewStateOrNew().let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getBlogPost(): BlogPost {
    getCurrentNewStateOrNew().let {
         return it.viewBlogFields.blogPost?.let {
              it
        }?: getDummyBlogPost()
    }
}

fun BlogViewModel.getDummyBlogPost(): BlogPost {
    return BlogPost(-1, "", "","","",1,"")
}