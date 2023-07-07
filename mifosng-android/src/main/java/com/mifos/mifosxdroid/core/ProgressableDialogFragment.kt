package com.mifos.mifosxdroid.core

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ViewFlipper
import androidx.fragment.app.DialogFragment
import com.mifos.mifosxdroid.R

/**
 * A [DialogFragment] that provides progress viewing functionality
 * with a ViewFlipper.
 */
open class ProgressableDialogFragment : DialogFragment() {
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
            if (viewFlipper?.displayedChild != childToFlipTo) {
                viewFlipper?.displayedChild = childToFlipTo
            }
        } catch (e: NullPointerException) {
            Log.w(
                javaClass.simpleName, "Couldn't show/hide progress bar. Are you sure your" +
                        " Fragment contains a ViewFlipper with ID \"view_flipper\"?"
            )
        }
    }

    fun hideKeyboard(view: View) {
        val inputManager = requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
        )
    }

    companion object {
        const val VIEW_FLIPPER_PROGRESS = 0
        const val VIEW_FLIPPER_CONTENT = 1
    }
}