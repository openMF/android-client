package com.mifos.core.datastore.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerConfig(
    val protocol: String,
    val endPoint: String,
    val apiPath: String,
    val port: String,
    val tenant: String,
) {
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