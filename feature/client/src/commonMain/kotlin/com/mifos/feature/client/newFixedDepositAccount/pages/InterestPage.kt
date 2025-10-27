/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.feature.client.newFixedDepositAccount.pages

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.step_interest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jetbrains.compose.resources.stringResource

// Data class for Rate Chart Item
data class RateChartItem(
    val amountRange: String,
    val description: String,
    val period: String,
    val interestRate: String
)

// Data class for Chart Information
data class ChartInformation(
    val name: String,
    val validFromDate: String,
    val endDate: String,
    val description: String,
    val groupingByAmount: Boolean
)

@Composable
fun InterestPage(onNext: () -> Unit) {
    var showRateChartModal by remember { mutableStateOf(false) }
    var showActionModal by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(-1) }

    // Sample data - in real implementation, this would come from ViewModel/Repository
    val chartInfo = remember {
        ChartInformation(
            name = "Yearly",
            validFromDate = "01 January 2025",
            endDate = "09 June 2025",
            description = "2025 year interest rate",
            groupingByAmount = false
        )
    }

    val rateChartItems = remember {
        mutableStateListOf(
            RateChartItem(
                amountRange = "$200 - $250",
                description = "First Period",
                period = "1 Years",
                interestRate = "7%"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart Information Section
        ChartInformationCard(
            chartInfo = chartInfo,
            onViewClick = { showRateChartModal = true }
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { /* Handle back - will be managed by stepper */ },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = rateChartItems.isNotEmpty(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Next")
            }
        }
    }

    // Rate Chart Modal
    if (showRateChartModal) {
        RateChartModal(
            rateChartItems = rateChartItems,
            onDismiss = { showRateChartModal = false },
            onItemClick = { index ->
                selectedItemIndex = index
                showRateChartModal = false
                showActionModal = true
            },
            onDownload = {
                // Handle download functionality
                // In real implementation, this would export chart data
            }
        )
    }

    // Action Modal (Edit/Delete options)
    if (showActionModal && selectedItemIndex >= 0) {
        ActionModal(
            item = rateChartItems[selectedItemIndex],
            onDismiss = {
                showActionModal = false
                showRateChartModal = true
            },
            onEditClick = {
                showActionModal = false
                showEditDialog = true
            },
            onDeleteClick = {
                showActionModal = false
                showDeleteConfirmation = true
            },
            onDownload = {
                // Handle download for this specific item
            }
        )
    }

    // Edit Dialog
    if (showEditDialog && selectedItemIndex >= 0) {
        EditRateChartDialog(
            item = rateChartItems[selectedItemIndex],
            onDismiss = {
                showEditDialog = false
                showRateChartModal = true
            },
            onConfirm = { updatedItem ->
                rateChartItems[selectedItemIndex] = updatedItem
                showEditDialog = false
                showRateChartModal = true
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation && selectedItemIndex >= 0) {
        DeleteConfirmationDialog(
            onDismiss = {
                showDeleteConfirmation = false
                showRateChartModal = true
            },
            onConfirm = {
                rateChartItems.removeAt(selectedItemIndex)
                showDeleteConfirmation = false
                showRateChartModal = true
            }
        )
    }
}

@Composable
private fun ChartInformationCard(
    chartInfo: ChartInformation,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow(label = "Name:", value = chartInfo.name)
            InfoRow(label = "Valid from Date:", value = chartInfo.validFromDate)
            InfoRow(label = "End Date:", value = chartInfo.endDate)
            InfoRow(label = "Description:", value = chartInfo.description)
            InfoRow(
                label = "Grouping by Amount:",
                value = if (chartInfo.groupingByAmount) "Yes" else "No"
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INTEREST RATE CHART",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF212121)
                )

                TextButton(
                    onClick = onViewClick,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "View",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            color = Color(0xFF757575),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = Color(0xFF212121),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RateChartModal(
    rateChartItems: List<RateChartItem>,
    onDismiss: () -> Unit,
    onItemClick: (Int) -> Unit,
    onDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Text(
                    text = "Rate Chart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF212121),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // Amount Range Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.dp, Color.Transparent),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "Amount Range",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Period",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "@Interest Rate",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "▲",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rate Chart Items List
                Column(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rateChartItems.forEachIndexed { index, item ->
                        RateChartItemCard(
                            item = item,
                            onClick = { onItemClick(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBDBDBD))
                    ) {
                        Text(
                            "Back",
                            color = Color(0xFF212121),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0)
                        )
                    ) {
                        Text(
                            "Download",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RateChartItemCard(
    item: RateChartItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.amountRange,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color(0xFF212121),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Start
                )
                Text(
                    text = item.period,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 13.sp,
                    color = Color(0xFF757575),
                    textAlign = TextAlign.Start
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.interestRate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "›",
                    fontSize = 24.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionModal(
    item: RateChartItem,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onDownload: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Rate Chart",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color(0xFF212121)
                )

                // Amount Range Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.dp, Color.Transparent),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "Amount Range",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Period",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "@Interest Rate",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "▲",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                // Item Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.amountRange,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 13.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = item.period,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 13.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Start
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.interestRate,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF4CAF50),
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "›",
                                fontSize = 24.sp,
                                color = Color(0xFF9E9E9E),
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    }
                }

                // Action Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onEditClick),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAFAFA)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(0xFF757575),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = "Edit",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    // Delete Option
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onDeleteClick),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(0.dp, Color.Transparent),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAFAFA)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFF757575),
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = "Delete",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,
                                color = Color(0xFF212121),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBDBDBD))
                    ) {
                        Text(
                            "Back",
                            color = Color(0xFF212121),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onDownload,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF5C6BC0)
                        )
                    ) {
                        Text(
                            "Download",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditRateChartDialog(
    item: RateChartItem,
    onDismiss: () -> Unit,
    onConfirm: (RateChartItem) -> Unit
) {
    var amountRange by remember { mutableStateOf(item.amountRange) }
    var description by remember { mutableStateOf(item.description) }
    var period by remember { mutableStateOf(item.period) }
    var interestRate by remember { mutableStateOf(item.interestRate.replace("%", "")) }
    var interestRateError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Edit Interest Rate",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = amountRange,
                    onValueChange = { amountRange = it },
                    label = { Text("Amount Range") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = period,
                    onValueChange = { period = it },
                    label = { Text("Period") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = interestRate,
                    onValueChange = {
                        interestRate = it
                        val rate = it.toDoubleOrNull()
                        interestRateError = rate == null || rate <= 0 || rate >= 100
                    },
                    label = { Text("Interest Rate (%)") },
                    isError = interestRateError,
                    supportingText = {
                        if (interestRateError) {
                            Text(
                                "Please enter a valid percentage (0-100)",
                                color = Color(0xFFD32F2F)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val rate = interestRate.toDoubleOrNull()
                    if (rate != null && rate > 0 && rate < 100) {
                        onConfirm(
                            RateChartItem(
                                amountRange = amountRange,
                                description = description,
                                period = period,
                                interestRate = "$interestRate%"
                            )
                        )
                    }
                },
                enabled = !interestRateError && interestRate.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C6BC0)
                )
            ) {
                Text("Save", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = Color(0xFF757575))
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Delete Rate Chart Entry",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF212121)
            )
        },
        text = {
            Text(
                "Are you sure you want to delete this interest rate entry? This action cannot be undone.",
                color = Color(0xFF757575),
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                )
            ) {
                Text("Delete", fontWeight = FontWeight.Medium)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancel", color = Color(0xFF757575))
            }
        }
    )
}
