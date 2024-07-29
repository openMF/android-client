/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
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
