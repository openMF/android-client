/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.offline

import kotlinx.coroutines.flow.Flow
import kpt.core.model.offline.DownloadProgress
import kpt.core.model.offline.DownloadTarget

/**
 * Downloads what an officer needs to work a center or group with no connection — the DOWNLOAD half of
 * offline (the upload half is `OfflineQueueRepository`).
 *
 * Ported from the `SyncCentersDialogViewModel` and `SyncGroupsDialogViewModel` of `6b66e8a43` (~1,300 lines of index
 * juggling across two copies). Same walk, one implementation:
 *  - a center → its groups (and the center's own accounts),
 *  - a group → its clients (and the group's own accounts),
 *  - a client → the client and its accounts,
 *  - every ACTIVE loan → the loan and its repayment template (so a repayment can be recorded offline),
 *  - every ACTIVE savings account → the account and its deposit + withdrawal templates.
 *
 * Each read goes through the SAME store, with the SAME key, that the screen reads — so "downloaded"
 * means the screen opens offline, not that some parallel table was filled. A selected center or group
 * is marked downloaded (`sync`), which its list shows.
 *
 * Cold flows: nothing runs until collected, and cancelling the collector stops the download.
 */
interface OfflineDownloadRepository {
    fun downloadCenters(centers: List<DownloadTarget>): Flow<DownloadProgress>

    fun downloadGroups(groups: List<DownloadTarget>): Flow<DownloadProgress>
}
