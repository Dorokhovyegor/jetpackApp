package com.dorokhov.jetpackapp.ui.main.blog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.models.BlogPost
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogStateEvent
import com.dorokhov.jetpackapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import javax.inject.Inject


class BlogFragment : BaseBlogFragment(), BlogListAdapter.Interaction {

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var recyclerAdapter: BlogListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        executeSearch()
        initRecyclerView()
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
            if (viewState != null) {
                recyclerAdapter.submitList(
                    list = viewState.blogFields.blogList,
                    isQueryExhausted = true
                )
            }
        })

    }

    private fun initRecyclerView() {
        blog_post_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingItemDecoration = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingItemDecoration)
            addItemDecoration(topSpacingItemDecoration)
            recyclerAdapter = BlogListAdapter(
                requestManager = requestManager,
                interaction = this@BlogFragment
            )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        println("$TAG: BlogFragment attempting to load next page ...")
                        // todo load next page using viewModel
                    }
                }
            })

            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        println("$TAG: itemClick: position: ${position}, blogInfo ${item}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // can leak memory
        blog_post_recyclerview.adapter = null
    }
}