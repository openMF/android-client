/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.updateServer

sealed interface UpdateServerConfigEvent {

    data class UpdateProtocol(val protocol: String) : UpdateServerConfigEvent

    data class UpdateEndPoint(val endPoint: String) : UpdateServerConfigEvent

    data class UpdateApiPath(val apiPath: String) : UpdateServerConfigEvent

    data class UpdatePort(val port: String) : UpdateServerConfigEvent

    data class UpdateTenant(val tenant: String) : UpdateServerConfigEvent

    data object UpdateServerConfig : UpdateServerConfigEvent
}
