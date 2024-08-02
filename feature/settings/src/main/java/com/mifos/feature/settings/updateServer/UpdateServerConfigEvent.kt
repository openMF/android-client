package com.mifos.feature.settings.updateServer

sealed interface UpdateServerConfigEvent {

    data class UpdateProtocol(val protocol: String) : UpdateServerConfigEvent

    data class UpdateEndPoint(val endPoint: String) : UpdateServerConfigEvent

    data class UpdateApiPath(val apiPath: String) : UpdateServerConfigEvent

    data class UpdatePort(val port: String) : UpdateServerConfigEvent

    data class UpdateTenant(val tenant: String) : UpdateServerConfigEvent

    data object UpdateServerConfig : UpdateServerConfigEvent
}