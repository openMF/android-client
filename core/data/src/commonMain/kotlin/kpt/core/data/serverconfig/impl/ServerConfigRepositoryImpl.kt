/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.serverconfig.impl

import kotlinx.coroutines.CoroutineScope
import kpt.core.base.data.annotation.FromStore
import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.mutation.MutationGateway
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.serverconfig.ServerConfigRepository
import kpt.core.datastore.prefs.ProjectPreferencesRepository
import kpt.core.model.utils.ServerConfig
import kpt.core.store.config.AppCacheKeys
import kpt.core.store.config.AppStoreIds
import org.mobilenativefoundation.store.store5.Store

@RepositoryBinding(binds = ServerConfigRepository::class)
internal class ServerConfigRepositoryImpl(
    private val gateway: MutationGateway,
    private val preferences: ProjectPreferencesRepository,
    @FromStore(AppStoreIds.ServerConfig) private val store: Store<Unit, ServerConfig>,
) : ServerConfigRepository {

    override fun serverConfigStream(scope: CoroutineScope): ScreenDataStream<ServerConfig> =
        store.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.ServerConfig.KEY,
            scope = scope,
            fetchPolicy = FetchPolicy.CACHE_ONLY,
        )

    // Through the gateway's local-mutation door, as `AuthCommandRepository.resumeStoredSession` is:
    // the write lands in the preference the store reads, so the stream re-emits on its own.
    override suspend fun updateServerConfig(config: ServerConfig) =
        gateway.localMutation(table = TABLE) { preferences.updateServerConfig(config) }

    private companion object {
        const val TABLE = "server_config"
    }
}
