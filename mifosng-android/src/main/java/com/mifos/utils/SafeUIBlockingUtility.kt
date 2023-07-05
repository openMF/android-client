/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * @author ishankhanna
 * Class To Block User Interface Safely for Asynchronous Network Calls
 * and/or Heavy Operations
 */
class SafeUIBlockingUtility {
    private var progressDialog: ProgressDialog
    private var context: Context

    constructor(context: Context) {
        this.context = context
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(utilityMessage)
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setTitle(utilityTitle)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    }

    constructor(context: Context, message: String?) {
        this.context = context
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.isIndeterminate = true
        progressDialog.setTitle(utilityTitle)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    }

    fun safelyBlockUI() {
        progressDialog.show()
    }

    fun safelyUnBlockUI() {
        progressDialog.dismiss()
    }

    fun safelyUnblockUIForFailure(tag: String?, message: String?) {
        progressDialog.dismiss()
        Toast.makeText(context, "Some Problem Executing Request", Toast.LENGTH_SHORT).show()
        Log.i(tag, message!!)
    }

    companion object {
        var utilityTitle = "Working"
        var utilityMessage = "Message"
    }
}