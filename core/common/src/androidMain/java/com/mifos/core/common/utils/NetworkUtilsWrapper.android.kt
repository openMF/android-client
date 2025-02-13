package com.mifos.core.common.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.MainThread

actual class NetworkUtilsWrapper actual constructor() {
    private var context: Context? = null

    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    @MainThread
    fun isNetworkConnected(): Boolean {
        val ctx = context ?: error("Call NetworkUtilsWrapper.initialize() first")
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}