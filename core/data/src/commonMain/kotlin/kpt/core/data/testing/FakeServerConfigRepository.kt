/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi::class)

package kpt.core.data.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.serverconfig.ServerConfigRepository
import kpt.core.model.utils.ServerConfig

/**
 * Test double for [ServerConfigRepository]: [saved] is the persisted preference, so a test reads
 * what a submit actually wrote. [failSaves] models the write failing.
 */
@ExperimentalDataTestingApi
class FakeServerConfigRepository(
    initial: ServerConfig = ServerConfig.DEFAULT,
) : ServerConfigRepository {

    val saved = MutableStateFlow(initial)

    var failSaves: Boolean = false

    override fun serverConfigStream(scope: CoroutineScope): ScreenDataStream<ServerConfig> =
        screenDataStreamForTesting(saved.map<ServerConfig, ScreenState<ServerConfig>> { ScreenState.Content(it) })

    override suspend fun updateServerConfig(config: ServerConfig): MutationResult<Unit> {
        if (failSaves) return MutationResult.Failed(cause = IllegalStateException("save failed"), rolledBack = false)
        saved.value = config
        return MutationResult.Applied(value = Unit, synced = true)
    }
}
