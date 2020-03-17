package com.dorokhov.jetpackapp.ui.main.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogStateEvent
import kotlinx.android.synthetic.main.fragment_blog.*


class BlogFragment : BaseBlogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goViewBlogFragment.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }
        subscribeObservers()
        executeSearch()
    }

    private fun executeSearch() {
        viewModel.setQuery("")
        viewModel.setStateEvent(
            BlogStateEvent.BlogSearchEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                stateChangeListener.onDataStateChange(dataState)
                dataState.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let {
                            println("$TAG: Blog Fragment, dataState ${it}")
                            viewModel.setBlogListData(it.blogFields.blogList)
                        }
                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            println("$TAG: BlogFragment, ViewState ${viewState}")
        })

    }

}