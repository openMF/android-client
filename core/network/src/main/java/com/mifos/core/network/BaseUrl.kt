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
        private val configs = BuildConfig.SERVER_CONFIG.split(",")

         val PROTOCOL_HTTPS = configs[0]
         val API_ENDPOINT = configs[1]
         val API_PATH = configs[2]
         val PORT = configs[3]
    }
}