/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerDetails

import androidclient.feature.center.generated.resources.Res
import androidclient.feature.center.generated.resources.feature_center_activate_center
import androidclient.feature.center.generated.resources.feature_center_activation_date
import androidclient.feature.center.generated.resources.feature_center_active_client
import androidclient.feature.center.generated.resources.feature_center_active_client_borrowers
import androidclient.feature.center.generated.resources.feature_center_active_client_loans
import androidclient.feature.center.generated.resources.feature_center_active_group_borrowers
import androidclient.feature.center.generated.resources.feature_center_active_group_loan
import androidclient.feature.center.generated.resources.feature_center_active_overdue_group_loans
import androidclient.feature.center.generated.resources.feature_center_add_savings_account
import androidclient.feature.center.generated.resources.feature_center_center_details
import androidclient.feature.center.generated.resources.feature_center_error_loading_centers
import androidclient.feature.center.generated.resources.feature_center_group_list
import androidclient.feature.center.generated.resources.feature_center_meeting_frequency
import androidclient.feature.center.generated.resources.feature_center_next_meeting_on
import androidclient.feature.center.generated.resources.feature_center_no_staff
import androidclient.feature.center.generated.resources.feature_center_staff_name
import androidclient.feature.center.generated.resources.feature_center_summary_info
import androidclient.feature.center.generated.resources.feature_center_unassigned
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Utils
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.groups.CenterInfo
import com.mifos.core.ui.util.DevicePreview
import com.mifos.room.entities.group.CenterWithAssociations
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun CenterDetailsScreen(
    onBackPressed: () -> Unit,
    onActivateCenter: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    groupList: (Int) -> Unit,
    viewModel: CenterDetailsViewModel = koinViewModel(),
) {
    val centerId by viewModel.centerId.collectAsStateWithLifecycle()
    val state by viewModel.centerDetailsUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadClientDetails(centerId)
    }

    CenterDetailsScreen(
        state = state,
        onBackPressed = onBackPressed,
        onMenuClick = {
            when (it) {
                MenuItems.ADD_SAVINGS_ACCOUNT -> addSavingsAccount(centerId)

                MenuItems.GROUP_LIST -> groupList(centerId)
            }
        },
        onRetryClick = {
            viewModel.loadClientDetails(centerId)
        },
        onActivateCenter = { onActivateCenter(centerId) },
    )
}

@Composable
internal fun CenterDetailsScreen(
    state: CenterDetailsUiState,
    onBackPressed: () -> Unit,
    onMenuClick: (MenuItems) -> Unit,
    onRetryClick: () -> Unit,
    onActivateCenter: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var centerActive by remember { mutableStateOf(true) }

    MifosScaffold(
        title = stringResource(Res.string.feature_center_center_details),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = MifosIcons.MoreVert, contentDescription = "Icon")
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_center_add_savings_account),
                    onClick = {
                        onMenuClick(MenuItems.ADD_SAVINGS_ACCOUNT)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_center_group_list),
                    onClick = {
                        onMenuClick(MenuItems.GROUP_LIST)
                        showMenu = false
                    },
                )
            }
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            if (!centerActive) {
                MifosButton(
                    onClick = { onActivateCenter() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(44.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_center_activate_center),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ) {
            when (state) {
                is CenterDetailsUiState.Error -> {
                    MifosSweetError(message = stringResource(Res.string.feature_center_error_loading_centers)) {
                        onRetryClick()
                    }
                }

                is CenterDetailsUiState.Loading -> MifosCircularProgress()

                is CenterDetailsUiState.CenterDetails -> {
                    CenterDetailsContent(
                        centerWithAssociations = state.centerWithAssociations,
                        centerInfo = state.centerInfo,
                        activateCenter = { centerActive = false },
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterDetailsContent(
    centerWithAssociations: CenterWithAssociations,
    centerInfo: CenterInfo,
    activateCenter: () -> Unit,
) {
    if (centerWithAssociations.active == false) {
        activateCenter()
    }
    Column {
        centerWithAssociations.name?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = it,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }
        MifosCenterDetailsText(
            icon = MifosIcons.Date,
            field = stringResource(Res.string.feature_center_activation_date),
            value = Utils.getStringOfDate(centerWithAssociations.activationDate),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Date,
            field = stringResource(Res.string.feature_center_next_meeting_on),
            value = if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
                stringResource(
                    Res.string.feature_center_unassigned,
                )
            } else {
                Utils.getStringOfDate(centerWithAssociations.collectionMeetingCalendar.nextTenRecurringDates[0])
            },
        )
        centerWithAssociations.collectionMeetingCalendar.humanReadable?.let {
            MifosCenterDetailsText(
                icon = MifosIcons.EventRepeat,
                field = stringResource(Res.string.feature_center_meeting_frequency),
                value = it,
            )
        }
        MifosCenterDetailsText(
            icon = MifosIcons.Person,
            field = stringResource(Res.string.feature_center_staff_name),
            value = if (centerWithAssociations.staffName != null) {
                centerWithAssociations.staffName.toString()
            } else {
                stringResource(
                    Res.string.feature_center_no_staff,
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(Res.string.feature_center_summary_info),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )

        MifosCenterDetailsText(
            icon = MifosIcons.Person,
            field = stringResource(Res.string.feature_center_active_client),
            value = centerInfo.activeClients.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Group,
            field = stringResource(Res.string.feature_center_active_group_loan),
            value = centerInfo.activeGroupLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Person,
            field = stringResource(Res.string.feature_center_active_client_loans),
            value = centerInfo.activeClientLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Group,
            field = stringResource(Res.string.feature_center_active_group_borrowers),
            value = centerInfo.activeGroupBorrowers.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Person,
            field = stringResource(Res.string.feature_center_active_client_borrowers),
            value = centerInfo.activeClientBorrowers.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Group,
            field = stringResource(Res.string.feature_center_active_overdue_group_loans),
            value = centerInfo.overdueGroupLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.Person,
            field = stringResource(Res.string.feature_center_active_group_loan),
            value = centerInfo.overdueClientLoans.toString(),
        )
    }
}

@Composable
private fun MifosCenterDetailsText(icon: ImageVector, field: String, value: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = "Icon",
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@DevicePreview
@Composable
private fun CenterDetailsScreenLoadingPreview() {
    CenterDetailsScreen(
        state = CenterDetailsUiState.Loading,
        onBackPressed = {},
        onMenuClick = {},
        onRetryClick = {},
        onActivateCenter = {},
    )
}

@DevicePreview
@Composable
private fun CenterDetailsScreenErrorPreview() {
    CenterDetailsScreen(
        state = CenterDetailsUiState.Error(Res.string.feature_center_error_loading_centers),
        onBackPressed = {},
        onMenuClick = {},
        onRetryClick = {},
        onActivateCenter = {},
    )
}

@DevicePreview
@Composable
private fun CenterDetailsScreenCenterDetailsPreview() {
    CenterDetailsScreen(
        state = CenterDetailsUiState.CenterDetails(CenterWithAssociations(), CenterInfo()),
        onBackPressed = {},
        onMenuClick = {},
        onRetryClick = {},
        onActivateCenter = {},
    )
}

enum class MenuItems {
    ADD_SAVINGS_ACCOUNT,
    GROUP_LIST,
}
