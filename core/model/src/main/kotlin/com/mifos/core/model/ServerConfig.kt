/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerConfig(
    val protocol: String,
    @SerializedName("end_point")
    val endPoint: String,
    @SerializedName("api_path")
    val apiPath: String,
    val port: String,
    val tenant: String,
) : Parcelable {
    companion object {
        val DEFAULT = ServerConfig(
            protocol = "https://",
            endPoint = "dev.mifos.io",
            apiPath = "/fineract-provider/api/v1/",
            port = "80",
            tenant = "default",
        )
    }
}

fun ServerConfig.getInstanceUrl(): String {
    return "$protocol$endPoint$apiPath"
}
