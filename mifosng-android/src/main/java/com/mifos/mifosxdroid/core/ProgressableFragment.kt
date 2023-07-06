package com.mifos.mifosxdroid.core

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ViewFlipper
import com.mifos.mifosxdroid.R

/**
 * A [MifosBaseFragment] that provides progress viewing functionality with a ViewFlipper.
 */
open class ProgressableFragment : MifosBaseFragment() {
    private var viewFlipper: ViewFlipper? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFlipper = view.findViewById(R.id.view_flipper)
        if (viewFlipper == null) {
            throw NullPointerException(
                "Are you sure your Fragment has a ViewFlipper with id " +
                        "\"view_flipper\"?"
            )
        }
    }

    /**
     * Switches the [ViewFlipper] either to the [ProgressBar] or to the content.
     *
     * @param show true to show [ProgressBar], false for content.
     */
    fun showProgress(show: Boolean) {
        try {
            val childToFlipTo = if (show) VIEW_FLIPPER_PROGRESS else VIEW_FLIPPER_CONTENT
            if (viewFlipper!!.displayedChild != childToFlipTo) {
                viewFlipper!!.displayedChild = childToFlipTo
            }
        } catch (e: NullPointerException) {
            Log.w(
                javaClass.simpleName, "Couldn't show/hide progress bar. Are you sure your" +
                        " Fragment contains a ViewFlipper with ID \"view_flipper\"?"
            )
        }
    }

    companion object {
        const val VIEW_FLIPPER_PROGRESS = 0
        const val VIEW_FLIPPER_CONTENT = 1
    }
}