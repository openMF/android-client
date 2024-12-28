/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.report.runReport

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.runreport.client.ClientReportTypeItem
import com.mifos.feature.report.R

@Composable
internal fun RunReportScreen(
    onBackPressed: () -> Unit,
    onReportClick: (ClientReportTypeItem) -> Unit,
    viewModel: RunReportViewModel = hiltViewModel(),
) {
    val state by viewModel.runReportUiState.collectAsStateWithLifecycle()
    var category by rememberSaveable { mutableStateOf(MenuItems.Client.name) }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories(
            reportCategory = category,
            genericResultSet = false,
            parameterType = true,
        )
    }

    RunReportScreen(
        state = state,
        onBackPressed = onBackPressed,
        onMenuClick = {
            category = it.name
            viewModel.fetchCategories(
                reportCategory = category,
                genericResultSet = false,
                parameterType = true,
            )
        },
        onRetry = {
            viewModel.fetchCategories(
                reportCategory = category,
                genericResultSet = false,
                parameterType = true,
            )
        },
        onReportClick = onReportClick,
    )
}

@Composable
internal fun RunReportScreen(
    state: RunReportUiState,
    onBackPressed: () -> Unit,
    onMenuClick: (MenuItems) -> Unit,
    onRetry: () -> Unit,
    onReportClick: (ClientReportTypeItem) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var menuTitle by rememberSaveable { mutableStateOf(MenuItems.Client.name) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackPressed() },
                    ) {
                        Icon(
                            imageVector = MifosIcons.arrowBack,
                            contentDescription = null,
                            tint = Black,
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.clickable {
                            showMenu = showMenu.not()
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = menuTitle,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = FontStyle.Normal,
                            ),
                            color = Black,
                            textAlign = TextAlign.Start,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (showMenu) MifosIcons.arrowUp else MifosIcons.arrowDown,
                            contentDescription = null,
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier.background(White),
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_client),
                            onClick = {
                                onMenuClick(MenuItems.Client)
                                menuTitle = MenuItems.Client.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_loan),
                            onClick = {
                                onMenuClick(MenuItems.Loan)
                                menuTitle = MenuItems.Loan.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_savings),
                            onClick = {
                                onMenuClick(MenuItems.Savings)
                                menuTitle = MenuItems.Savings.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_fund),
                            onClick = {
                                onMenuClick(MenuItems.Fund)
                                menuTitle = MenuItems.Fund.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_accounting),
                            onClick = {
                                onMenuClick(MenuItems.Accounting)
                                menuTitle = MenuItems.Accounting.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_xbrl),
                            onClick = {
                                onMenuClick(MenuItems.XBRL)
                                menuTitle = MenuItems.XBRL.name
                                showMenu = false
                            },
                        )
                        MifosMenuDropDownItem(
                            option = stringResource(id = R.string.feature_report_all),
                            onClick = {
                                onMenuClick(MenuItems.All)
                                menuTitle = MenuItems.All.name
                                showMenu = false
                            },
                        )
                    }
                },
                actions = {},
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = White,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is RunReportUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                    onRetry()
                }

                is RunReportUiState.Loading -> {
                    MifosCircularProgress()
                }

                is RunReportUiState.RunReports -> {
                    RunReportContent(runReports = state.runReports, onReportClick = onReportClick)
                }
            }
        }
    }
}

@Composable
private fun RunReportContent(
    runReports: List<ClientReportTypeItem>,
    modifier: Modifier = Modifier,
    onReportClick: (ClientReportTypeItem) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(runReports.size) { index ->
            RunReportCardItem(report = runReports[index], onReportClick = onReportClick)
        }
    }
}

@Composable
private fun RunReportCardItem(
    report: ClientReportTypeItem,
    modifier: Modifier = Modifier,
    onReportClick: (ClientReportTypeItem) -> Unit,
) {
    OutlinedCard(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                onReportClick(report)
            },
        colors = CardDefaults.cardColors(White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    16.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .background(BlueSecondary, CircleShape),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.feature_report_ic_report_item),
                    contentDescription = null,
                    tint = Black,
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
            ) {
                report.reportName?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = Black,
                        ),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = report.reportType.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                    Text(
                        text = report.reportCategory.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal,
                            color = DarkGray,
                        ),
                    )
                }
            }
        }
    }
}

enum class MenuItems {
    Client,
    Loan,
    Savings,
    Fund,
    Accounting,
    XBRL,
    All,
}

class RunReportUiStateProvider : PreviewParameterProvider<RunReportUiState> {

    override val values: Sequence<RunReportUiState>
        get() = sequenceOf(
            RunReportUiState.Loading,
            RunReportUiState.Error(R.string.feature_report_failed_to_fetch_reports),
            RunReportUiState.RunReports(sampleRunReports),
        )
}

@Preview(showBackground = true)
@Composable
private fun RunReportPreview(
    @PreviewParameter(RunReportUiStateProvider::class) state: RunReportUiState,
) {
    RunReportScreen(
        state = state,
        onBackPressed = {},
        onMenuClick = {},
        onRetry = {},
        onReportClick = {},
    )
}

val sampleRunReports = List(10) {
    ClientReportTypeItem(
        reportName = "Report $it",
        reportType = "Type $it",
        reportCategory = "Category $it",
    )
}
