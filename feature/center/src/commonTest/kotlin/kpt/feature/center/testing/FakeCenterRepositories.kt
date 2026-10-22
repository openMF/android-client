/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
@file:OptIn(kpt.core.base.store.screen.ExperimentalScreenDataStreamTestingApi::class)

package kpt.feature.center.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.screenDataStreamForTesting
import kpt.core.data.center.CenterRepository
import kpt.core.data.group.GroupRepository
import kpt.core.database.center.entity.CenterEntity
import kpt.core.database.group.entity.GroupEntity

/**
 * In-memory repositories for ViewModel- and screen-level tests.
 *
 * Both hand back REAL [ScreenDataStream]s via the sanctioned `screenDataStreamForTesting` factory
 * rather than a parallel fake of the state machine — so a test exercises the same Loading /
 * Content / Empty / Error transitions production does. Mirrors `FakeNoteRepository`.
 */
internal class FakeCenterRepository(
    initial: ScreenState<List<CenterEntity>> = ScreenState.Loading,
) : CenterRepository {
    val state = MutableStateFlow(initial)
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 4)

    fun emit(vararg centers: CenterEntity) {
        state.value =
            if (centers.isEmpty()) ScreenState.Empty else ScreenState.Content(centers.toList())
    }

    override fun centersStream(scope: CoroutineScope): ScreenDataStream<List<CenterEntity>> =
        screenDataStreamForTesting(state, refreshTrigger = refreshes)
}

internal class FakeGroupRepository(
    initial: ScreenState<List<GroupEntity>> = ScreenState.Loading,
) : GroupRepository {
    val state = MutableStateFlow(initial)
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 4)

    fun emit(vararg groups: GroupEntity) {
        state.value =
            if (groups.isEmpty()) ScreenState.Empty else ScreenState.Content(groups.toList())
    }

    override fun groupsStream(scope: CoroutineScope): ScreenDataStream<List<GroupEntity>> =
        screenDataStreamForTesting(state, refreshTrigger = refreshes)
}

internal fun center(
    id: Int,
    name: String? = "Center $id",
    accountNo: String? = "C$id",
    officeName: String? = "Head Office",
    active: Boolean? = true,
) = CenterEntity(
    id = id,
    name = name,
    accountNo = accountNo,
    officeName = officeName,
    active = active,
)

internal fun group(
    id: Int,
    centerId: Int?,
    name: String? = "Group $id",
) = GroupEntity(id = id, centerId = centerId, name = name, accountNo = "G$id")
