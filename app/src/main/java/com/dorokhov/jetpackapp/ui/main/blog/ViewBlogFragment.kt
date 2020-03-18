package com.dorokhov.jetpackapp.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.models.BlogPost
import com.dorokhov.jetpackapp.ui.AreYouSureCallBack
import com.dorokhov.jetpackapp.ui.UIMessage
import com.dorokhov.jetpackapp.ui.UIMessageType
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogStateEvent
import com.dorokhov.jetpackapp.ui.main.blog.viewmodel.*
import com.dorokhov.jetpackapp.util.DateUtils
import com.dorokhov.jetpackapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.android.synthetic.main.fragment_view_blog.*

class ViewBlogFragment : BaseBlogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
        checkIsAuthorOfBlogPost()
        stateChangeListener.expandAppBar()

        delete_button?.setOnClickListener {
            confirmDeleteRequest()
        }
    }

    private fun confirmDeleteRequest() {
        val callBack: AreYouSureCallBack = object: AreYouSureCallBack {
            override fun proceed() {
                deleteBlogPost()
            }

            override fun cancel() {
                //ignore
            }
        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure),
                UIMessageType.AreYouSureDialog(callBack)
            )
        )
    }

    private fun checkIsAuthorOfBlogPost() {
        viewModel.setIsAuthorOfBLogPost(false)
        viewModel.setStateEvent(BlogStateEvent.CheckAuthorOfBlogPost())
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState.data?.let { data->
                data.data?.getContentIfNotHandled()?.let { viewState->
                    viewModel.setIsAuthorOfBLogPost(
                        viewState.viewBlogFields.isAuthorOfBlogPost
                    )
                }

                data.response?.peekContent()?.let { response ->
                    if (response.message.equals(SUCCESS_BLOG_DELETED)) {
                        viewModel.removeDeletedBlogPost()
                        findNavController().popBackStack()
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.viewBlogFields.blogPost?.let { blogPost ->
                setBlogProperties(blogPost)
            }

            if (viewState.viewBlogFields.isAuthorOfBlogPost) {
                adaptViewToAuthorMode()
            }
        })
    }

    private fun adaptViewToAuthorMode() {
        activity?.invalidateOptionsMenu() // перепровирть меню
        delete_button.visibility = View.VISIBLE


    }

    private fun setBlogProperties(blogPost: BlogPost) {
        requestManager.load(blogPost.image)
            .into(blog_image)

        blog_title.setText(blogPost.title)
        blog_author.setText(blogPost.username)
        blog_update_date.setText(DateUtils.convertLongToStringDate(blogPost.date_updated))
        blog_body.setText(blogPost.body)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        // "check if user is author of blog post"
        if (viewModel.isAuthorOfBlogPost()) {
            inflater.inflate(R.menu.edit_view_menu, menu)
        }

    }

    private fun deleteBlogPost() {
        viewModel.setStateEvent(BlogStateEvent.DeleteBlogPostEvent())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //"check if user is author of blog post"
        if (viewModel.isAuthorOfBlogPost()) {
            when (item.itemId) {
                R.id.edit -> {
                    navUpdateBlogFragment()
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun navUpdateBlogFragment() {
        try {
            viewModel.setUpdateBlogFields(
                viewModel.getBlogPost().title,
                viewModel.getBlogPost().body,
                viewModel.getBlogPost().image.toUri()
            )
            findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}