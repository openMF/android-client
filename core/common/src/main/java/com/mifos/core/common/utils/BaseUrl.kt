package com.mifos.core.common.utils

import com.mifos.core.common.BuildConfig

object BaseUrl {

    // "/" in the last of the base url always

    const val PROTOCOL_HTTPS = BuildConfig.PROTOCOL_HTTPS

    const val API_ENDPOINT = BuildConfig.API_ENDPOINT

    const val API_PATH = BuildConfig.API_PATH

    const val PORT = BuildConfig.PORT

    const val TENANT = BuildConfig.TENANT
}