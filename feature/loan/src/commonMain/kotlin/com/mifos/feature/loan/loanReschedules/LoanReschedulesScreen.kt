/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanReschedules

import kpt.feature.loan.generated.resources.Res
import kpt.feature.loan.generated.resources.feature_loan_add_reschedule_cd
import kpt.feature.loan.generated.resources.feature_loan_reschedule_approve_confirm
import kpt.feature.loan.generated.resources.feature_loan_reschedule_approve_message
import kpt.feature.loan.generated.resources.feature_loan_reschedule_approve_title
import kpt.feature.loan.generated.resources.feature_loan_reschedule_cancel
import kpt.feature.loan.generated.resources.feature_loan_reschedule_delete_confirm
import kpt.feature.loan.generated.resources.feature_loan_reschedule_delete_message
import kpt.feature.loan.generated.resources.feature_loan_reschedule_delete_title
import kpt.feature.loan.generated.resources.feature_loan_reschedule_failure_title
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_actions
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_from_date
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_na
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_number
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_reason
import kpt.feature.loan.generated.resources.feature_loan_reschedule_label_status
import kpt.feature.loan.generated.resources.feature_loan_reschedule_ok
import kpt.feature.loan.generated.resources.feature_loan_reschedules_empty
import kpt.feature.loan.generated.resources.feature_loan_reschedules_title
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.mifos.core.common.utils.ApiDateFormatter
import com.mifos.core.designsystem.component.MifosDialogBox
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTableRow
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.model.objects.account.loan.reschedules.LoanRescheduleResponse
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosProgressIndicatorOverlay
import com.mifos.core.ui.components.MifosStatusDialog
import com.mifos.core.ui.components.ResultStatus
import com.mifos.core.ui.util.EventsEffect
import com.mifos.feature.loan.createLoanReschedules.navigateToLoanRescheduleFormScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.LocalKptColors
import kpt.core.base.designsystem.theme.LocalKptTypography
import kpt.core.base.designsystem.theme.LocalKptShapes
import kpt.core.base.designsystem.theme.LocalKptSpacing

@Composable
internal fun LoanReschedulesScreenRoute(
    navController: NavController,
    viewModel: LoanReschedulesViewModel = koinViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            LoanReschedulesEvent.NavigateBack -> navigateBack()
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.trySendAction(LoanReschedulesAction.RefreshList)
        }
    }

    RescheduleListScreen(
        navController = navController,
        state = state,
        onAddClick = { navController.navigateToLoanRescheduleFormScreen(viewModel.loanId) },
        onAction = viewModel::trySendAction,
    )
}

@Composable
internal fun RescheduleListScreen(
    navController: NavController,
    state: LoanReschedulesUiState,
    onAddClick: () -> Unit,
    onAction: (LoanReschedulesAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MifosBreadcrumbNavBar(navController = navController)

        Box(modifier = Modifier.weight(1f)) {
            RescheduleListContent(
                state = state,
                onAddClick = onAddClick,
                onRetry = { onAction(LoanReschedulesAction.OnRetryFetching) },
                onDeleteClick = { item -> onAction(LoanReschedulesAction.OnDeleteIconClick(item)) },
                onApproveClick = { item -> onAction(LoanReschedulesAction.OnApproveIconClick(item)) },
                onAction = onAction,
            )
        }
    }
    RescheduleListDialogContent(
        dialogState = state.dialogState,
        onAction = onAction,
        onRetry = { onAction(LoanReschedulesAction.OnRetryFetching) },
    )
}

@Composable
internal fun RescheduleListContent(
    state: LoanReschedulesUiState,
    onAddClick: () -> Unit,
    onRetry: () -> Unit,
    onDeleteClick: (LoanRescheduleResponse) -> Unit,
    onApproveClick: (LoanRescheduleResponse) -> Unit,
    onAction: (LoanReschedulesAction) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            Text(
                modifier = Modifier.padding(
                    top = DesignToken.padding.medium,
                    start = LocalKptSpacing.current.md,
                    end = LocalKptSpacing.current.md,
                ),
                text = stringResource(Res.string.feature_loan_reschedules_title),
                style = LocalKptTypography.current.labelLarge,
            )

            Spacer(Modifier.height(DesignToken.spacing.medium))

            if (state.history.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    val scrollState = rememberScrollState()
                    val textColor = LocalKptColors.current.onBackground
                    val colWidths = listOf(
                        DesignToken.sizes.tableCellWidthExtraSmall,
                        DesignToken.sizes.tableCellWidthMediumLarge,
                        DesignToken.sizes.tableCellWidthExtraLarge,
                        DesignToken.sizes.tableCellWidthExtraExtraLarge,
                        DesignToken.sizes.tableCellWidthMedium,
                    )

                    val headers = listOf(
                        stringResource(Res.string.feature_loan_reschedule_label_number),
                        stringResource(Res.string.feature_loan_reschedule_label_from_date),
                        stringResource(Res.string.feature_loan_reschedule_label_reason),
                        stringResource(Res.string.feature_loan_reschedule_label_status),
                        stringResource(Res.string.feature_loan_reschedule_label_actions),
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = LocalKptSpacing.current.md)
                            .horizontalScroll(scrollState),
                    ) {
                        MifosTableRow(
                            cells = headers.map { label ->
                                {
                                    RescheduleTableCell(
                                        text = label,
                                        style = LocalKptTypography.current.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        textColor = textColor,
                                    )
                                }
                            },
                            widths = colWidths,
                            backgroundColor = lerp(
                                LocalKptColors.current.surface,
                                LocalKptColors.current.primary,
                                0.3f,
                            ),
                            edgeOffset = DesignToken.padding.medium,
                            cornerShape = DesignToken.shapes.topMedium,
                        )

                        state.history.forEachIndexed { index, item ->
                            val isLast = index == state.history.lastIndex
                            val formattedDate = item.rescheduleFromDate
                                ?.takeIf { it.size >= 3 }
                                ?.let { ApiDateFormatter.formatFromList(it) }
                                ?: stringResource(Res.string.feature_loan_reschedule_label_na)

                            val reasonText = item.rescheduleReasonCodeValue?.name
                                ?: stringResource(Res.string.feature_loan_reschedule_label_na)

                            val statusText = item.statusEnum?.value
                                ?: stringResource(Res.string.feature_loan_reschedule_label_na)

                            val statusEnum = item.statusEnum.toRescheduleStatus()

                            MifosTableRow(
                                cells = buildList {
                                    add {
                                        RescheduleTableCell(
                                            (index + 1).toString(),
                                            LocalKptTypography.current.bodySmall,
                                            FontWeight.Normal,
                                            textColor,
                                        )
                                    }
                                    add {
                                        RescheduleTableCell(
                                            formattedDate,
                                            LocalKptTypography.current.bodySmall,
                                            FontWeight.Normal,
                                            textColor,
                                        )
                                    }
                                    add {
                                        RescheduleTableCell(
                                            reasonText,
                                            LocalKptTypography.current.bodySmall,
                                            FontWeight.Normal,
                                            textColor,
                                        )
                                    }
                                    add {
                                        RescheduleStatusCell(
                                            statusText,
                                            statusEnum,
                                            LocalKptTypography.current.bodySmall,
                                        )
                                    }
                                    add {
                                        RescheduleActionsCell(
                                            item.statusEnum?.pendingApproval == true,
                                            { onDeleteClick(item) },
                                            { onApproveClick(item) },
                                        )
                                    }
                                },
                                widths = colWidths,
                                backgroundColor = LocalKptColors.current.surface,
                                edgeOffset = DesignToken.padding.medium,
                                cornerShape = if (isLast) DesignToken.shapes.bottomMedium else RectangleShape,
                                showBottomBorder = !isLast,
                            )
                        }
                    }
                    Spacer(Modifier.height(LocalKptSpacing.current.xl))
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.feature_loan_reschedules_empty),
                        style = LocalKptTypography.current.bodyMedium,
                        color = LocalKptColors.current.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(LocalKptSpacing.current.md),
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(DesignToken.padding.largeIncreased),
            containerColor = LocalKptColors.current.primary,
        ) {
            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = stringResource(Res.string.feature_loan_add_reschedule_cd),
                tint = AppColors.customWhite,
            )
        }
    }
}

@Composable
private fun RescheduleListDialogContent(
    dialogState: LoanReschedulesUiState.DialogState?,
    onAction: (LoanReschedulesAction) -> Unit,
    onRetry: () -> Unit,
) {
    when (val dialog = dialogState) {
        is LoanReschedulesUiState.DialogState.Loading -> MifosProgressIndicatorOverlay()

        is LoanReschedulesUiState.DialogState.FetchingFailed -> {
            MifosSweetError(
                message = stringResource(dialog.messageRes),
                isRetryEnabled = true,
                onclick = onRetry,
            )
        }

        is LoanReschedulesUiState.DialogState.ConfirmDelete -> {
            MifosDialogBox(
                showDialogState = true,
                title = stringResource(Res.string.feature_loan_reschedule_delete_title),
                message = stringResource(Res.string.feature_loan_reschedule_delete_message),
                confirmButtonText = stringResource(Res.string.feature_loan_reschedule_delete_confirm),
                dismissButtonText = stringResource(Res.string.feature_loan_reschedule_cancel),
                onConfirm = { onAction(LoanReschedulesAction.ConfirmDelete(dialog.rescheduleId)) },
                onDismiss = { onAction(LoanReschedulesAction.DismissDialog) },
            )
        }

        is LoanReschedulesUiState.DialogState.ConfirmApprove -> {
            MifosDialogBox(
                showDialogState = true,
                title = stringResource(Res.string.feature_loan_reschedule_approve_title),
                message = stringResource(Res.string.feature_loan_reschedule_approve_message),
                confirmButtonText = stringResource(Res.string.feature_loan_reschedule_approve_confirm),
                dismissButtonText = stringResource(Res.string.feature_loan_reschedule_cancel),
                onConfirm = { onAction(LoanReschedulesAction.ConfirmApprove(dialog.rescheduleId)) },
                onDismiss = { onAction(LoanReschedulesAction.DismissDialog) },
            )
        }

        is LoanReschedulesUiState.DialogState.ActionError -> {
            MifosStatusDialog(
                status = ResultStatus.FAILURE,
                btnText = stringResource(Res.string.feature_loan_reschedule_ok),
                onConfirm = { onAction(LoanReschedulesAction.DismissDialog) },
                onDismissRequest = { onAction(LoanReschedulesAction.DismissDialog) },
                successTitle = "",
                successMessage = "",
                failureTitle = stringResource(Res.string.feature_loan_reschedule_failure_title),
                failureMessage = stringResource(dialog.messageRes),
                showAsDialog = true,
            )
        }
        null -> { }
    }
}

@Composable
private fun RescheduleTableCell(
    text: String,
    style: TextStyle,
    fontWeight: FontWeight,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LocalKptSpacing.current.sm, horizontal = LocalKptSpacing.current.xs),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = style,
            fontWeight = fontWeight,
            color = textColor,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RescheduleStatusCell(
    statusText: String,
    status: RescheduleStatusCode,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (status) {
        RescheduleStatusCode.PENDING -> AppColors.loanPendingStatus
        RescheduleStatusCode.APPROVED -> LocalKptColors.current.primary
        RescheduleStatusCode.REJECTED -> LocalKptColors.current.error
        RescheduleStatusCode.UNKNOWN -> AppColors.loanUnknownStatus
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LocalKptSpacing.current.sm, horizontal = LocalKptSpacing.current.xs),
    ) {
        Box(
            modifier = Modifier
                .size(DesignToken.sizes.iconMinyMiny)
                .clip(DesignToken.shapes.dp2)
                .background(statusColor),
        )
        Spacer(modifier = Modifier.width(LocalKptSpacing.current.sm))
        Text(
            text = statusText,
            color = statusColor,
            style = style,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun RescheduleActionsCell(
    isPending: Boolean,
    onDeleteClick: () -> Unit,
    onApproveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LocalKptSpacing.current.sm),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LocalKptSpacing.current.sm, horizontal = LocalKptSpacing.current.xs),
    ) {
        if (isPending) {
            Box(
                modifier = Modifier
                    .size(DesignToken.sizes.iconLarge)
                    .clip(LocalKptShapes.current.extraSmall)
                    .background(LocalKptColors.current.error)
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = MifosIcons.Delete,
                    contentDescription = stringResource(Res.string.feature_loan_reschedule_delete_confirm),
                    tint = AppColors.customWhite,
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
                )
            }

            Box(
                modifier = Modifier
                    .size(DesignToken.sizes.iconLarge)
                    .clip(LocalKptShapes.current.extraSmall)
                    .background(LocalKptColors.current.primary)
                    .clickable { onApproveClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = MifosIcons.Check,
                    contentDescription = stringResource(Res.string.feature_loan_reschedule_approve_confirm),
                    tint = AppColors.customWhite,
                    modifier = Modifier.size(DesignToken.sizes.iconSmall),
                )
            }
        } else {
            Text(
                text = stringResource(Res.string.feature_loan_reschedule_label_na),
                style = LocalKptTypography.current.bodySmall,
                color = LocalKptColors.current.outline,
                modifier = Modifier.padding(start = LocalKptSpacing.current.sm),
            )
        }
    }
}
