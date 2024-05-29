/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network

import com.mifos.core.common.BuildConfig
import com.mifos.core.common.utils.asServerConfig

/**
 * @author fomenkoo
 */
class BaseUrl {

    // "/" in the last of the base url always
    companion object {
        private val configs = BuildConfig.DEMO_SERVER_CONFIG.asServerConfig()

         val PROTOCOL_HTTPS = configs.protocol
         val API_ENDPOINT = configs.endPoint
         val API_PATH = configs.apiPath
         val PORT = configs.port
    }
}