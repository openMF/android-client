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
        const val PROTOCOL_HTTPS = "https://"
        const val API_ENDPOINT = "demo.mifos.community"
        const val API_PATH = "/fineract-provider/api/v1/"
        const val PORT = "80"
    }
}