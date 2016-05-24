package com.mifos.mifosxdroid.core;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by rajanmaurya on 16/4/2016.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to
    // load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current
    // scroll position before loading more.
    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading && (totalItemCount > previousTotal + 1)) {
            loading = false;
            previousTotal = totalItemCount;
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem +
                visibleThreshold)) {
            // End has been reached
            // Do something
            current_page++;
            onLoadMore(current_page);
            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}