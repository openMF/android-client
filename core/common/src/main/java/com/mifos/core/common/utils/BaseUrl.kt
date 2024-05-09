package com.mifos.core.common.utils

import com.mifos.core.common.BuildConfig

object BaseUrl {

    private val configs = BuildConfig.DEMO_SERVER_CONFIG.split(",")

    // "/" in the last of the base url always

     val PROTOCOL_HTTPS = configs[0]

     val API_ENDPOINT = configs[1]

     val API_PATH = configs[2]

     val PORT = configs[3]

     val TENANT = configs[4]
}