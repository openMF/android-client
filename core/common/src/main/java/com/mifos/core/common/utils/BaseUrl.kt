package com.mifos.core.common.utils

import com.mifos.core.common.BuildConfig

object BaseUrl {

    private val configs = BuildConfig.DEMO_SERVER_CONFIG.asServerConfig()

    // "/" in the last of the base url always

    val PROTOCOL_HTTPS = configs.protocol

    val API_ENDPOINT = configs.endPoint

    val API_PATH = configs.apiPath

    val PORT = configs.port

    val TENANT = configs.tenant
}