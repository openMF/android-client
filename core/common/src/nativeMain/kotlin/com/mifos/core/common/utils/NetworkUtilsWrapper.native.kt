package com.mifos.core.common.utils

actual class NetworkUtilsWrapper actual constructor() {
    fun isNetworkConnected(): Boolean = Network.isOnline()
}