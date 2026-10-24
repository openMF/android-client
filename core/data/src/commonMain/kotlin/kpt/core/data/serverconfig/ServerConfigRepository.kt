/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.serverconfig

import kotlinx.coroutines.CoroutineScope
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.model.utils.ServerConfig

/**
 * The Fineract server configuration, read and written through `core/data` like every other
 * value a screen shows — never by a ViewModel reaching into `core/datastore` itself.
 */
interface ServerConfigRepository {

    /** Local-only (`CACHE_ONLY`): the configuration is a device preference with no network source. */
    fun serverConfigStream(scope: CoroutineScope): ScreenDataStream<ServerConfig>

    /** Persists [config]; the stream re-emits it. */
    suspend fun updateServerConfig(config: ServerConfig): MutationResult<Unit>
}
