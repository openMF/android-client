package com.mifos.mifosxdroid.core

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by rajanmaurya on 16/4/2016.
 */
abstract class EndlessRecyclerOnScrollListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private var previousTotal = 0 // The total number of items in the dataset after the last load
    private var loading = true // True if we are still waiting for the last set of data to

    // load.
    private val visibleThreshold = 1 // The minimum amount of items to have below your current

    // scroll position before loading more.
    private var currentPage = 1
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.itemCount

        // If user refreshed a layout
        if (previousTotal > totalItemCount) {
            previousTotal = 0
        }
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        if (loading && totalItemCount > previousTotal + 1) {
            loading = false
            previousTotal = totalItemCount
        }
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            // End has been reached
            currentPage++
            onLoadMore(currentPage)
            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)

    companion object {
        val TAG = EndlessRecyclerOnScrollListener::class.java.simpleName
    }
}