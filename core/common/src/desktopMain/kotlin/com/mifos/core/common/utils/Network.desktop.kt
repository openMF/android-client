package com.mifos.core.common.utils

actual object Network {
      fun isOnline(): Boolean = try {
        java.net.NetworkInterface.getNetworkInterfaces()
            .toList()
            .any { it.isUp && !it.isLoopback }
    } catch (e: Exception) {
        false
    }
}