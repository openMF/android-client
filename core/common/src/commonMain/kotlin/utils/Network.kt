/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package utils

import android.content.Context
import android.net.NetworkCapabilities

/**
 * Created by Aditya Gupta on 11/02/24.
 */

object Network {

    fun isOnline(context: android.content.Context): Boolean {
        val connectivityManager =
            android.content.Context.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val capabilities =
            android.net.ConnectivityManager.getNetworkCapabilities(android.net.ConnectivityManager.getActiveNetwork)
        if (capabilities equals null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}
