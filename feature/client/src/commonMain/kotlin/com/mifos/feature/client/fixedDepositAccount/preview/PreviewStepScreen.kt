/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.feature.client.fixedDepositAccount.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography

@Composable
fun PreviewStepScreen(
    state: PreviewStepState,
    onAction: (PreviewStepAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(DesignToken.padding.large)
    ) {
        // Step Title
        Text(
            text = "Preview",
            style = MifosTypography.headlineMedium,
            modifier = Modifier.padding(bottom = DesignToken.padding.medium)
        )

        Text(
            text = "Review all entered details before submission",
            style = MifosTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = DesignToken.padding.large)
        )

        // Scrollable Content
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium)
        ) {
            // Details Section
            item {
                PreviewSection(
                    title = "Details",
                    isExpanded = state.expandedSections.contains("details"),
                    onToggle = { onAction(PreviewStepAction.ToggleSection("details")) }
                ) {
                    PreviewField("Product Name", state.productName)
                    PreviewField("Submission Date", state.submissionDate)
                    PreviewField("External ID", state.externalId)
                    PreviewField("Field Officer", state.fieldOfficer)
                }
            }

            // Currency Section
            item {
                PreviewSection(
                    title = "Currency",
                    isExpanded = state.expandedSections.contains("currency"),
                    onToggle = { onAction(PreviewStepAction.ToggleSection("currency")) }
                ) {
                    PreviewField("Currency", state.currency)
                    PreviewField("Currency Multiple", state.currencyMultiple)
                    PreviewField("Decimal Places", state.decimalPlaces)
                }
            }

            // Terms Section
            item {
                PreviewSection(
                    title = "Terms",
                    isExpanded = state.expandedSections.contains("terms"),
                    onToggle = { onAction(PreviewStepAction.ToggleSection("terms")) }
                ) {
                    PreviewField("Fixed Deposit Amount", state.depositAmount)
                    PreviewField("Deposit Period", state.depositPeriod)
                    PreviewField("Interest Compounding Period", state.compoundingPeriod)
                    PreviewField("Interest Posting Period", state.postingPeriod)
                    PreviewField("Interest Calculation Method", state.calculationMethod)
                    PreviewField("Days in Year", state.daysInYear)
                }
            }

            // Settings Section
            item {
                PreviewSection(
                    title = "Settings",
                    isExpanded = state.expandedSections.contains("settings"),
                    onToggle = { onAction(PreviewStepAction.ToggleSection("settings")) }
                ) {
                    PreviewField("Lock-in Period", state.lockInPeriod)
                    PreviewField("Minimum Deposit Term", state.minDepositTerm)
                    PreviewField("Transfer Interest to Savings", state.transferToSavings)
                    PreviewField("Maturity Instructions", state.maturityInstructions)
                    PreviewField("Apply Penal Interest", state.applyPenalInterest)
                }
            }

            // Interest Rate Chart Section
            item {
                PreviewSection(
                    title = "Interest Rate Chart",
                    isExpanded = state.expandedSections.contains("interest_chart"),
                    onToggle = { onAction(PreviewStepAction.ToggleSection("interest_chart")) }
                ) {
                    PreviewField("Name", state.chartName)
                    PreviewField("Valid From", state.chartValidFrom)
                    PreviewField("Valid To", state.chartValidTo)
                    PreviewField("Description", state.chartDescription)
                    PreviewField("Grouping by Amount", state.chartGroupingByAmount)
                    
                    Spacer(modifier = Modifier.height(DesignToken.spacing.small))
                    
                    // Rate Chart Table
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Rate Chart Details",
                            style = MifosTypography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.small)
                        ) {
                            TextButton(
                                onClick = { onAction(PreviewStepAction.ViewRateChart) }
                            ) {
                                Icon(
                                    imageVector = MifosIcons.Visibility,
                                    contentDescription = "View"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("View")
                            }
                            TextButton(
                                onClick = { onAction(PreviewStepAction.DownloadRateChart) }
                            ) {
                                Icon(
                                    imageVector = MifosIcons.Download,
                                    contentDescription = "Download"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download")
                            }
                        }
                    }
                }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = DesignToken.padding.large),
            horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.medium)
        ) {
            MifosOutlinedButton(
                onClick = { onAction(PreviewStepAction.NavigateBack) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            
            MifosButton(
                onClick = { onAction(PreviewStepAction.Submit) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Submit")
            }
        }
    }

    // Rate Chart Modal
    if (state.showRateChartModal) {
        InterestRateChartModal(
            rateChartEntries = state.rateChartEntries,
            onDismiss = { onAction(PreviewStepAction.DismissRateChartModal) },
            onEdit = { entry -> onAction(PreviewStepAction.EditRateChartEntry(entry)) },
            onDelete = { entry -> onAction(PreviewStepAction.DeleteRateChartEntry(entry)) },
            onDownload = { onAction(PreviewStepAction.DownloadRateChart) }
        )
    }

    // Delete Confirmation Dialog
    if (state.showDeleteConfirmation != null) {
        AlertDialog(
            onDismissRequest = { onAction(PreviewStepAction.DismissDeleteConfirmation) },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete this rate chart entry?") },
            confirmButton = {
                TextButton(
                    onClick = { onAction(PreviewStepAction.ConfirmDelete) }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onAction(PreviewStepAction.DismissDeleteConfirmation) }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun PreviewSection(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(DesignToken.padding.medium)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MifosTypography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) MifosIcons.ExpandLess else MifosIcons.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(DesignToken.spacing.medium))
                content()
            }
        }
    }
}

@Composable
private fun PreviewField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = DesignToken.spacing.extraSmall),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MifosTypography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value.ifEmpty { "-" },
            style = MifosTypography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InterestRateChartModal(
    rateChartEntries: List<RateChartEntry>,
    onDismiss: () -> Unit,
    onEdit: (RateChartEntry) -> Unit,
    onDelete: (RateChartEntry) -> Unit,
    onDownload: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(DesignToken.padding.large)
            ) {
                Text(
                    text = "Interest Rate Chart",
                    style = MifosTypography.headlineSmall,
                    modifier = Modifier.padding(bottom = DesignToken.padding.medium)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(DesignToken.spacing.small)
                ) {
                    items(rateChartEntries) { entry ->
                        RateChartEntryCard(
                            entry = entry,
                            onEdit = { onEdit(entry) },
                            onDelete = { onDelete(entry) }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = DesignToken.padding.medium),
                    horizontalArrangement = Arrangement.spacedBy(DesignToken.spacing.small)
                ) {
                    MifosOutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    MifosButton(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = MifosIcons.Download,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download")
                    }
                }
            }
        }
    }
}

@Composable
private fun RateChartEntryCard(
    entry: RateChartEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.padding.medium)
        ) {
            PreviewField("Amount Range", entry.amountRange)
            PreviewField("Period", entry.period)
            PreviewField("Interest Rate", "${entry.interestRate}%")
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = DesignToken.spacing.small),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) {
                    Icon(
                        imageVector = MifosIcons.Edit,
                        contentDescription = "Edit"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
                TextButton(onClick = onDelete) {
                    Icon(
                        imageVector = MifosIcons.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}
@Composable
fun PreviewStepScreenContainer(
    navigateBack: () -> Unit,
    onSubmit: () -> Unit,
    viewModel: PreviewStepViewModel = koinViewModel()
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    
    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            is PreviewStepEvent.NavigateToChargesStep -> navigateBack()
            is PreviewStepEvent.NavigateToConfirmation -> onSubmit()
            is PreviewStepEvent.ShowMessage -> {
                // Show snackbar or toast
            }
            is PreviewStepEvent.ShowError -> {
                // Show error dialog
            }
            is PreviewStepEvent.NavigateToEditRateChart -> {
                // Handle edit navigation
            }
        }
    }
    
    PreviewStepScreen(
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } }
    )
}

// Data Models
data class PreviewStepState(
    // Details
    val productName: String = "",
    val submissionDate: String = "",
    val externalId: String = "",
    val fieldOfficer: String = "",
    
    // Currency
    val currency: String = "",
    val currencyMultiple: String = "",
    val decimalPlaces: String = "",
    
    // Terms
    val depositAmount: String = "",
    val depositPeriod: String = "",
    val compoundingPeriod: String = "",
    val postingPeriod: String = "",
    val calculationMethod: String = "",
    val daysInYear: String = "",
    
    // Settings
    val lockInPeriod: String = "",
    val minDepositTerm: String = "",
    val transferToSavings: String = "",
    val maturityInstructions: String = "",
    val applyPenalInterest: String = "",
    
    // Interest Rate Chart
    val chartName: String = "",
    val chartValidFrom: String = "",
    val chartValidTo: String = "",
    val chartDescription: String = "",
    val chartGroupingByAmount: String = "",
    val rateChartEntries: List<RateChartEntry> = emptyList(),
    
    // UI State
    val expandedSections: Set<String> = setOf("details", "currency", "terms", "settings", "interest_chart"),
    val showRateChartModal: Boolean = false,
    val showDeleteConfirmation: RateChartEntry? = null
)

data class RateChartEntry(
    val id: String,
    val amountRange: String,
    val period: String,
    val interestRate: Double
)

sealed class PreviewStepAction {
    data class ToggleSection(val sectionId: String) : PreviewStepAction()
    data object ViewRateChart : PreviewStepAction()
    data object DownloadRateChart : PreviewStepAction()
    data object DismissRateChartModal : PreviewStepAction()
    data class EditRateChartEntry(val entry: RateChartEntry) : PreviewStepAction()
    data class DeleteRateChartEntry(val entry: RateChartEntry) : PreviewStepAction()
    data object ConfirmDelete : PreviewStepAction()
    data object DismissDeleteConfirmation : PreviewStepAction()
    data object NavigateBack : PreviewStepAction()
    data object Submit : PreviewStepAction()
}