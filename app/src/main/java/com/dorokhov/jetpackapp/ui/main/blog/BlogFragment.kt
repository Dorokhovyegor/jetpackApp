package com.dorokhov.jetpackapp.ui.main.blog

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.models.BlogPost
import com.dorokhov.jetpackapp.ui.DataState
import com.dorokhov.jetpackapp.ui.main.blog.state.BlogViewState
import com.dorokhov.jetpackapp.ui.main.blog.viewmodel.*
import com.dorokhov.jetpackapp.util.ErrorHandling
import com.dorokhov.jetpackapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*


class BlogFragment : BaseBlogFragment(), BlogListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var recyclerAdapter: BlogListAdapter
    private lateinit var searchView: androidx.appcompat.widget.SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        initRecyclerView()
        subscribeObservers()

        if (savedInstanceState == null) {
            viewModel.loadFirstPage()
        }

    }

    private fun resetUI() {
        blog_post_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyBoard()
        focusable_view.requestFocus()
    }

    private fun onBlogSearchOrFilter() {
        viewModel.loadFirstPage()
        resetUI()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {
                handlePagination(dataState)
                stateChangeListener.onDataStateChange(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            println("$TAG: BlogFragment, ViewState ${viewState}")
            if (viewState != null) {
                recyclerAdapter.submitList(
                    list = viewState.blogFields.blogList,
                    isQueryExhausted = viewState.blogFields.isQueryExhausted
                )
            }
        })

    }

    private fun initSearchView(menu: Menu) {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView =
                menu.findItem(R.id.action_search).actionView as androidx.appcompat.widget.SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }


        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = v.text.toString()
                viewModel.setQuery(searchQuery).let {
                    onBlogSearchOrFilter()
                }
            }
            true
        }

        searchView.findViewById<View>(R.id.search_go_btn).setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            viewModel.setQuery(searchQuery).let {
                onBlogSearchOrFilter()
            }
        }
    }

    private fun handlePagination(dataState: DataState<BlogViewState>) {
        dataState.data?.let {
            it.data?.let {
                it.getContentIfNotHandled()?.let {
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }

        dataState.error?.let { event ->
            event.peekContent().response.message?.let {
                if (ErrorHandling.isPaginationDone(it)) {
                    //handle the error. we don't have to display it in UI
                    event.getContentIfNotHandled()

                    viewModel.setQueryExhausted(true)
                }
            }
        }
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        println("$TAG: BlogFragment attempting to load next page ...")
                        // todo load next page using viewModel
                        viewModel.nextPage()
                    }
                }
            })

            adapter = recyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        viewModel.setBlogPost(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // can leak memory
        blog_post_recyclerview.adapter = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)

    }

    override fun onRefresh() {
        onBlogSearchOrFilter()
        swipe_refresh.isRefreshing = false
    }
}