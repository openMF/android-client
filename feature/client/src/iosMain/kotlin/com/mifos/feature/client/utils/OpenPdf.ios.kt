package com.mifos.feature.client.utils

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun openPdf(path: String) {
    val url = NSURL.fileURLWithPath(path)
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    }
}
