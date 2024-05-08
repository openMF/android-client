/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

/**
 * @author fomenkoo
 */
class BaseUrl {
    // "/" in the last of the base url always
    companion object {
        const val PROTOCOL_HTTPS = BuildConfig.PROTOCOL_HTTPS
        const val API_ENDPOINT = BuildConfig.API_ENDPOINT
        const val API_PATH = BuildConfig.API_PATH
        const val PORT = BuildConfig.PORT
    }
}