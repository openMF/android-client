package com.mifos.core.common.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.SCNetworkReachabilityFlags
import platform.darwin.*
import kotlin.experimental.ExperimentalNativeApi

actual object Network {
     @OptIn(ExperimentalNativeApi::class)
     fun isOnline(): Boolean = Platform.isNetworkAvailable() // Implement in native code
}

private fun Platform.isNetworkAvailable() {
    //
}
