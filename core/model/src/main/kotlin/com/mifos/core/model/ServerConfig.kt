package com.mifos.core.model

import com.google.gson.annotations.SerializedName

data class ServerConfig(
    val protocol: String,
    @SerializedName("end_point")
    val endPoint: String,
    @SerializedName("api_path")
    val apiPath: String,
    val port: String,
    val tenant: String
)
