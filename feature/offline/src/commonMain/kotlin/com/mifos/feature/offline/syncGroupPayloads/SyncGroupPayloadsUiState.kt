/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.offline.syncGroupPayloads

import androidclient.feature.offline.generated.resources.Res
import androidclient.feature.offline.generated.resources.feature_offline_all_groups_synced
import androidclient.feature.offline.generated.resources.feature_offline_ic_assignment_turned_in_black_24dp
import androidclient.feature.offline.generated.resources.feature_offline_no_group_payload_to_sync
import com.mifos.room.entities.group.GroupPayloadEntity
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupPayloadsUiState {
    data object Loading : SyncGroupPayloadsUiState()
    data class Success(val emptyState: GroupPayloadEmptyState? = null) : SyncGroupPayloadsUiState()
    data class Error(val messageResId: StringResource) : SyncGroupPayloadsUiState()
}

enum class GroupPayloadEmptyState(
    val messageResId: StringResource,
    val iconResId: DrawableResource,
) {
    ALL_SYNCED(
        messageResId = Res.string.feature_offline_all_groups_synced,
        iconResId = Res.drawable.feature_offline_ic_assignment_turned_in_black_24dp,
    ),
    NOTHING_TO_SYNC(
        messageResId = Res.string.feature_offline_no_group_payload_to_sync,
        iconResId = Res.drawable.feature_offline_ic_assignment_turned_in_black_24dp,
    ),
}

val dummyGroupPayloads = listOf(
    GroupPayloadEntity(
        id = 1,
        errorMessage = null,
        officeId = 101,
        active = true,
        activationDate = "2024-01-01",
        submittedOnDate = "2024-01-01",
        externalId = "EXT001",
        name = "Group 1",
        locale = "en",
        dateFormat = "dd MMM yyyy",
    ),
    GroupPayloadEntity(
        id = 2,
        errorMessage = "Error syncing group",
        officeId = 102,
        active = false,
        activationDate = "2024-02-01",
        submittedOnDate = "2024-02-01",
        externalId = "EXT002",
        name = "Group 2",
        locale = "en",
        dateFormat = "dd MMM yyyy",
    ),
    GroupPayloadEntity(
        id = 3,
        errorMessage = null,
        officeId = 103,
        active = true,
        activationDate = "2024-03-01",
        submittedOnDate = "2024-03-01",
        externalId = "EXT003",
        name = "Group 3",
        locale = "en",
        dateFormat = "dd MMM yyyy",
    ),
)
