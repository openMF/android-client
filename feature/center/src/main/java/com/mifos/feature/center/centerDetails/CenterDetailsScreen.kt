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

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Utils
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.modelobjects.groups.CenterInfo
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.feature.center.R

@Composable
internal fun CenterDetailsScreen(
    onBackPressed: () -> Unit,
    onActivateCenter: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    groupList: (Int) -> Unit,
    viewModel: CenterDetailsViewModel = hiltViewModel(),
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
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_center_center),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                modifier = Modifier.background(White),
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                MifosMenuDropDownItem(
                    option = stringResource(id = R.string.feature_center_add_savings_account),
                    onClick = {
                        onMenuClick(MenuItems.ADD_SAVINGS_ACCOUNT)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(id = R.string.feature_center_group_list),
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
                Button(
                    onClick = { onActivateCenter() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(44.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary,
                    ),
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_center_activate_center),
                        fontSize = 16.sp,
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
                    MifosSweetError(message = stringResource(id = R.string.feature_center_error_loading_centers)) {
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
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Black,
                textAlign = TextAlign.Center,
            )
        }
        MifosCenterDetailsText(
            icon = MifosIcons.date,
            field = stringResource(id = R.string.feature_center_activation_date),
            value = Utils.getStringOfDate(centerWithAssociations.activationDate),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.date,
            field = stringResource(id = R.string.feature_center_next_meeting_on),
            value = if (centerWithAssociations.collectionMeetingCalendar.calendarInstanceId == null) {
                stringResource(
                    id = R.string.feature_center_unassigned,
                )
            } else {
                Utils.getStringOfDate(centerWithAssociations.collectionMeetingCalendar.nextTenRecurringDates[0])
            },
        )
        centerWithAssociations.collectionMeetingCalendar.humanReadable?.let {
            MifosCenterDetailsText(
                icon = MifosIcons.eventRepeat,
                field = stringResource(id = R.string.feature_center_meeting_frequency),
                value = it,
            )
        }
        MifosCenterDetailsText(
            icon = MifosIcons.person,
            field = stringResource(id = R.string.feature_center_staff_name),
            value = if (centerWithAssociations.staffName != null) {
                centerWithAssociations.staffName.toString()
            } else {
                stringResource(
                    R.string.feature_center_no_staff,
                )
            },
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(id = R.string.feature_center_summary_info),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            color = Black,
            textAlign = TextAlign.Center,
        )

        MifosCenterDetailsText(
            icon = MifosIcons.person,
            field = stringResource(id = R.string.feature_center_active_client),
            value = centerInfo.activeClients.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.group,
            field = stringResource(id = R.string.feature_center_active_group_loan),
            value = centerInfo.activeGroupLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.person,
            field = stringResource(id = R.string.feature_center_active_client_loans),
            value = centerInfo.activeClientLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.group,
            field = stringResource(id = R.string.feature_center_active_group_borrowers),
            value = centerInfo.activeGroupBorrowers.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.person,
            field = stringResource(id = R.string.feature_center_active_client_borrowers),
            value = centerInfo.activeClientBorrowers.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.group,
            field = stringResource(id = R.string.feature_center_active_overdue_group_loans),
            value = centerInfo.overdueGroupLoans.toString(),
        )
        MifosCenterDetailsText(
            icon = MifosIcons.person,
            field = stringResource(id = R.string.feature_center_active_group_loan),
            value = centerInfo.overdueClientLoans.toString(),
        )
    }
}

@Composable
private fun MifosCenterDetailsText(icon: ImageVector, field: String, value: String) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = null,
            tint = DarkGray,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
            color = Black,
            textAlign = TextAlign.Start,
        )
        Text(

            text = value,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
            ),
            color = DarkGray,
            textAlign = TextAlign.Start,
        )
    }
}

private class CenterDetailsUiStateProvider : PreviewParameterProvider<CenterDetailsUiState> {

    override val values: Sequence<CenterDetailsUiState>
        get() = sequenceOf(
            CenterDetailsUiState.Loading,
            CenterDetailsUiState.Error(R.string.feature_center_error_loading_centers),
            CenterDetailsUiState.CenterDetails(CenterWithAssociations(), CenterInfo()),
        )
}

@Preview(showBackground = true)
@Composable
private fun CenterDetailsScreenPreview(
    @PreviewParameter(CenterDetailsUiStateProvider::class) state: CenterDetailsUiState,
) {
    CenterDetailsScreen(
        state = state,
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
