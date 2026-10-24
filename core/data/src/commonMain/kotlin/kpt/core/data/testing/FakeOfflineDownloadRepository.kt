/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.testing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kpt.core.data.offline.OfflineDownloadRepository
import kpt.core.model.offline.DownloadProgress
import kpt.core.model.offline.DownloadTarget

/** Records what was asked for and replays one progress step per target, then a finished one. */
@ExperimentalDataTestingApi
class FakeOfflineDownloadRepository : OfflineDownloadRepository {

    val centersRequested = mutableListOf<List<DownloadTarget>>()
    val groupsRequested = mutableListOf<List<DownloadTarget>>()

    override fun downloadCenters(centers: List<DownloadTarget>): Flow<DownloadProgress> =
        replay(centers).also { centersRequested += centers }

    override fun downloadGroups(groups: List<DownloadTarget>): Flow<DownloadProgress> =
        replay(groups).also { groupsRequested += groups }

    private fun replay(targets: List<DownloadTarget>) = flow {
        targets.forEachIndexed { i, t -> emit(DownloadProgress(completed = i, total = targets.size, current = t.name)) }
        emit(DownloadProgress(completed = targets.size, total = targets.size, finished = true))
    }
}
