package com.mifos.mifosxdroid.views

import android.content.Context
import android.content.res.Resources.Theme
import android.util.AttributeSet
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

class CustomSpinner : AppCompatSpinner {
    interface OnSpinnerEventsListener {
        /**
         * Callback triggered when the spinner was opened.
         */
        fun onSpinnerOpened(spinner: Spinner, isItemListLarge: Boolean)

        /**
         * Callback triggered when the spinner was closed.
         */
        fun onSpinnerClosed(spinner: Spinner)
    }

    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false
    override fun performClick(): Boolean {
        // register that the Spinner was opened so we have a status
        // indicator for when the container holding this Spinner may lose focus
        mOpenInitiated = true
        if (mListener != null) {
            if (super.getAdapter().count >= 10) {
                mListener!!.onSpinnerOpened(this, true)
                return true
            } else {
                mListener!!.onSpinnerOpened(this, false)
            }
        }
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasBeenOpened() && hasFocus) {
            performClosedEvent()
        }
    }

    /**
     * Register the listener which will listen for events.
     */
    fun setSpinnerEventsListener(
        onSpinnerEventsListener: OnSpinnerEventsListener?
    ) {
        mListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside if needed.
     */
    private fun performClosedEvent() {
        mOpenInitiated = false
        if (mListener != null) {
            mListener!!.onSpinnerClosed(this)
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    private fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }

    constructor(context: Context) : super(context) {}
    constructor(context: Context, mode: Int) : super(context, mode) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, mode: Int) : super(
        context,
        attrs,
        defStyleAttr,
        mode
    ) {
    }

    constructor(
        context: Context, attrs: AttributeSet?,
        defStyleAttr: Int, mode: Int, popupTheme: Theme?
    ) : super(context, attrs, defStyleAttr, mode, popupTheme) {
    }
}