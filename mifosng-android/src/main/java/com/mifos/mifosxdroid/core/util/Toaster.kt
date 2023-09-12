/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.core.util

import android.view.View
import android.widget.TextView
import com.google.android.material.R
import com.google.android.material.snackbar.Snackbar
import com.mifos.application.App

/**
 * @author fomenkoo
 */
object Toaster {
    const val INDEFINITE = Snackbar.LENGTH_INDEFINITE
    const val LONG = Snackbar.LENGTH_LONG
    const val SHORT = Snackbar.LENGTH_SHORT

    @JvmStatic
    fun show(view: View?, text: String?, duration: Int) {
        if (view != null && text != null) {
            val snackbar = Snackbar.make(view, text, duration)
            val sbView = snackbar.view
            val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
            textView.textSize = 12f
            snackbar.setAction("OK") { snackbar.dismiss() }
            snackbar.show()
        }
    }

    @JvmStatic
    fun show(view: View?, res: Int, duration: Int) {
        show(view, App.context?.resources?.getString(res), duration)
    }

    @JvmStatic
    fun show(view: View?, string: String?) {
        show(view, string, Snackbar.LENGTH_LONG)
    }

    @JvmStatic
    fun show(view: View?, res: Int) {
        show(view, App.context?.resources?.getString(res))
    }
}