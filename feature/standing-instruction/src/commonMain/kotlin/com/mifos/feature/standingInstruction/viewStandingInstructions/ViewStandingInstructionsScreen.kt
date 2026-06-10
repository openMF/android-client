/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.standingInstruction.viewStandingInstructions

import androidclient.feature.standing_instruction.generated.resources.Res
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_no_standing_instructions_found_message
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_standing_instructions
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_amount
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_beneficiary
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_client
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_client_id
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_from_account
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_to_account
import androidclient.feature.standing_instruction.generated.resources.feature_standing_instructions_table_header_validity
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DataState
import com.mifos.core.designsystem.component.BasicDialogState
import com.mifos.core.designsystem.component.MifosBasicDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.feature.standingInstructions.viewStandingInstructions.StandingInstructionRowData
import com.mifos.feature.standingInstructions.viewStandingInstructions.StandingInstructionTableData
import com.mifos.feature.standingInstructions.viewStandingInstructions.ViewStandingInstructionsAction
import com.mifos.feature.standingInstructions.viewStandingInstructions.ViewStandingInstructionsEvent
import com.mifos.feature.standingInstructions.viewStandingInstructions.ViewStandingInstructionsState
import com.mifos.feature.standingInstructions.viewStandingInstructions.ViewStandingInstructionsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ViewStandingInstructionsScreen(
    viewModel: ViewStandingInstructionsViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                ViewStandingInstructionsEvent.NavigateBack -> navigateBack()
            }
        }
    }

    ViewStandingInstructionsContent(
        state = state,
        onAction = viewModel::trySendAction,
    )

    ViewStandingInstructionsDialogs(
        dialogState = state.dialogState,
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun ViewStandingInstructionsContent(
    state: ViewStandingInstructionsState,
    onAction: (ViewStandingInstructionsAction) -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_standing_instructions_standing_instructions),
        snackbarHostState = snackbarHostState,
        onBackPressed = { onAction(ViewStandingInstructionsAction.OnNavigateBack) },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (state.dataState) {
                is DataState.Error -> {
                    MifosSweetError(
                        message = state.dataState.message,
                        onclick = { onAction(ViewStandingInstructionsAction.Retry) },
                    )
                }

                is DataState.Success -> {
                    state.tableData?.let { data ->
                        ViewStandingInstructionsData(
                            tableData = data,
                        )
                    }
                }

                DataState.Loading -> {
                    MifosProgressIndicator()
                }
            }
        }
    }
}

@Composable
internal fun ViewStandingInstructionsDialogs(
    dialogState: ViewStandingInstructionsState.DialogState?,
    onAction: (ViewStandingInstructionsAction) -> Unit,
) {
    when (dialogState) {
        is ViewStandingInstructionsState.DialogState.Error -> {
            MifosBasicDialog(
                visibilityState = BasicDialogState.Shown(
                    message = dialogState.message,
                    title = dialogState.title,
                ),
                onDismissRequest = { onAction(ViewStandingInstructionsAction.DismissErrorDialog) },
            )
        }
        null -> Unit
    }
}

@Composable
private fun ViewStandingInstructionsData(
    tableData: StandingInstructionTableData,
) {
    val scrollState = rememberScrollState()

    val smallWidth = DesignToken.sizes.tableCellWidthSmall
    val mediumWidth = DesignToken.sizes.tableCellWidthMedium
    val largeWidth = DesignToken.sizes.tableCellWidthLarge

    val columnWidths = remember(smallWidth, mediumWidth, largeWidth) {
        listOf(
            smallWidth,
            largeWidth,
            mediumWidth,
            largeWidth,
            mediumWidth,
            mediumWidth,
            mediumWidth,
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = DesignToken.padding.large),
        horizontalAlignment = Alignment.Start,
    ) {
        if (tableData.rows.isNotEmpty()) {
            stickyHeader {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    StandingInstructionTableHeader(columnWidths)
                }
            }
            items(tableData.rows) { row ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                ) {
                    StandingInstructionRow(row, columnWidths)
                }
            }
        } else {
            item {
                MifosEmptyCard(
                    msg = stringResource(Res.string.feature_standing_instructions_no_standing_instructions_found_message),
                    title = stringResource(Res.string.feature_standing_instructions_no_standing_instructions_found_message),
                )
            }
        }
    }
}

@Composable
private fun StandingInstructionTableHeader(widths: List<Dp>) {
    val headers = listOf(
        stringResource(Res.string.feature_standing_instructions_table_header_client_id),
        stringResource(Res.string.feature_standing_instructions_table_header_client),
        stringResource(Res.string.feature_standing_instructions_table_header_from_account),
        stringResource(Res.string.feature_standing_instructions_table_header_beneficiary),
        stringResource(Res.string.feature_standing_instructions_table_header_to_account),
        stringResource(Res.string.feature_standing_instructions_table_header_amount),
        stringResource(Res.string.feature_standing_instructions_table_header_validity),
    )

    MifosTableRow(
        cells = headers.map { header ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = DesignToken.padding.small,
                            horizontal = DesignToken.padding.extraSmall,
                        ),
                ) {
                    Text(
                        text = header,
                        style = MifosTypography.labelMediumEmphasized,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        widths = widths,
        backgroundColor = MaterialTheme.colorScheme.primary,
        edgeOffset = DesignToken.spacing.medium,
        cornerShape = DesignToken.shapes.topMedium,
    )
}

@Composable
private fun StandingInstructionRow(
    row: StandingInstructionRowData,
    widths: List<Dp>,
) {
    val rowValues = listOf(
        row.id.toString(),
        row.client,
        row.fromAccount,
        row.beneficiary,
        row.toAccount,
        row.amount,
        row.validity,
    )

    MifosTableRow(
        cells = rowValues.map { value ->
            {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = DesignToken.padding.small,
                            horizontal = DesignToken.padding.extraSmall,
                        ),
                ) {
                    Text(text = value, style = MifosTypography.labelMedium)
                }
            }
        },
        widths = widths,
        backgroundColor = Color.Transparent,
        edgeOffset = DesignToken.spacing.medium,
    )
}
