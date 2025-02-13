package com.mifos.core.common.utils

import java.net.InetSocketAddress
import java.net.Socket

actual class NetworkUtilsWrapper actual constructor() {
     fun isNetworkConnected(): Boolean = Network.isOnline()
}