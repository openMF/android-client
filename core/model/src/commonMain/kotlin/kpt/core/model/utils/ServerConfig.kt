/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.model.utils

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val protocol: String,
    @SerialName("end_point")
    val endPoint: String,
    @SerialName("api_path")
    val apiPath: String,
    val port: String,
    val tenant: String,
) {
    companion object {
        /**
         * REPOINTED 2026-10-22 to `sandbox.mifos.community` — operator decision.
         *
         * Must stay equal to the `mifos` access point in `app-profile/app.yaml`, which is the SoT;
         * `ServerConfigAccessPointParityTest` fails if the two diverge. The previous value was
         * `apis.mifos.community` + `/1.0/field/v1/` + tenant `mifos-bank-1` — the field-officer
         * gateway, whose Fineract is down (502 on every path, including its own health endpoint).
         * Sandbox is plain Fineract, so path AND tenant change with the host.
         */
        val DEFAULT = ServerConfig(
            protocol = "https://",
            endPoint = "sandbox.mifos.community",
            apiPath = "/fineract-provider/api/v1/",
            port = "443",
            tenant = "default",
        )

        val LOCALHOST = ServerConfig(
            protocol = "http://",
            endPoint = "localhost",
            apiPath = "/fineract-provider/api/v1/",
            port = "8080",
            tenant = "default",
        )
    }
}

fun ServerConfig.getInstanceUrl(): String {
    return "$protocol$endPoint$apiPath"
}
