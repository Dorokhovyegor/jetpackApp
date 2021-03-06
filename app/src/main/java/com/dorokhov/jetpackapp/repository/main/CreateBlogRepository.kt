package com.dorokhov.jetpackapp.repository.main

import com.dorokhov.jetpackapp.api.main.OpenApiMainService
import com.dorokhov.jetpackapp.persistance.BlogPostDao
import com.dorokhov.jetpackapp.repository.JobManager
import com.dorokhov.jetpackapp.session.SessionManager
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    var sessionManager: SessionManager
): JobManager("CreateBlogRepository")