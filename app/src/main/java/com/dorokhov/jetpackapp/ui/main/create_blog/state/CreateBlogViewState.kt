package com.dorokhov.jetpackapp.ui.main.create_blog.state

import android.net.Uri

data class CreateBlogViewState(

    // create blog fragment vars
   var blogFields: NewBlogFields = NewBlogFields()

) {

    data class NewBlogFields(
        var newBlogTitle: String? = null,
        var newBlogBody: String? = null,
        var newImageUri: Uri? = null
    )

}
