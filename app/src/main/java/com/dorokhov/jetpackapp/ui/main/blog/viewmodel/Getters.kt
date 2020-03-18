package com.dorokhov.jetpackapp.ui.main.blog.viewmodel

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