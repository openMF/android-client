package com.mifos.feature.groups.sync_group_payloads

import com.mifos.core.objects.group.GroupPayload
import com.mifos.feature.groups.R


/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class SyncGroupPayloadsUiState {
    data object Loading: SyncGroupPayloadsUiState()
    data class Success(val emptyState: GroupPayloadEmptyState? = null): SyncGroupPayloadsUiState()
    data class Error(val messageResId: Int): SyncGroupPayloadsUiState()
}

enum class GroupPayloadEmptyState(
    val messageResId: Int,
    val iconResId: Int
) {
    ALL_SYNCED(
        messageResId = R.string.feature_groups_all_groups_synced,
        iconResId = R.drawable.feature_groups_ic_assignment_turned_in_black_24dp
    ),
    NOTHING_TO_SYNC (
        messageResId = R.string.feature_groups_no_group_payload_to_sync,
        iconResId = R.drawable.feature_groups_ic_assignment_turned_in_black_24dp
    )
}

val dummyGroupPayloads = listOf(
    GroupPayload(
        id = 1,
        errorMessage = null,
        officeId = 101,
        active = true,
        activationDate = "2024-01-01",
        submittedOnDate = "2024-01-01",
        externalId = "EXT001",
        name = "Group 1",
        locale = "en",
        dateFormat = "dd MMM yyyy"
    ),
    GroupPayload(
        id = 2,
        errorMessage = "Error syncing group",
        officeId = 102,
        active = false,
        activationDate = "2024-02-01",
        submittedOnDate = "2024-02-01",
        externalId = "EXT002",
        name = "Group 2",
        locale = "en",
        dateFormat = "dd MMM yyyy"
    ),
    GroupPayload(
        id = 3,
        errorMessage = null,
        officeId = 103,
        active = true,
        activationDate = "2024-03-01",
        submittedOnDate = "2024-03-01",
        externalId = "EXT003",
        name = "Group 3",
        locale = "en",
        dateFormat = "dd MMM yyyy"
    )
)