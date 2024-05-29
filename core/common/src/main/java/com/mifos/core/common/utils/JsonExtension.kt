package com.mifos.core.common.utils

import com.google.gson.Gson
import com.mifos.core.model.ServerConfig

fun String.asServerConfig(): ServerConfig {
    val jsonString = this.replace("'", "\"")
    return Gson().fromJson(jsonString, ServerConfig::class.java)
}