/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.core_ui_account_no
import androidclient.core.ui.generated.resources.core_ui_action
import androidclient.core.ui.generated.resources.core_ui_amount_paid
import androidclient.core.ui.generated.resources.core_ui_balance
import androidclient.core.ui.generated.resources.core_ui_calculation_type
import androidclient.core.ui.generated.resources.core_ui_cancelled
import androidclient.core.ui.generated.resources.core_ui_completed
import androidclient.core.ui.generated.resources.core_ui_description
import androidclient.core.ui.generated.resources.core_ui_document_id
import androidclient.core.ui.generated.resources.core_ui_document_key
import androidclient.core.ui.generated.resources.core_ui_document_type
import androidclient.core.ui.generated.resources.core_ui_due
import androidclient.core.ui.generated.resources.core_ui_due_as_of
import androidclient.core.ui.generated.resources.core_ui_effective_from
import androidclient.core.ui.generated.resources.core_ui_end_date
import androidclient.core.ui.generated.resources.core_ui_identify_documents
import androidclient.core.ui.generated.resources.core_ui_last_active
import androidclient.core.ui.generated.resources.core_ui_loan_balance
import androidclient.core.ui.generated.resources.core_ui_loan_product
import androidclient.core.ui.generated.resources.core_ui_name
import androidclient.core.ui.generated.resources.core_ui_original_loan
import androidclient.core.ui.generated.resources.core_ui_outstanding
import androidclient.core.ui.generated.resources.core_ui_owner_external_id
import androidclient.core.ui.generated.resources.core_ui_paid
import androidclient.core.ui.generated.resources.core_ui_pending
import androidclient.core.ui.generated.resources.core_ui_purchase_price_ratio
import androidclient.core.ui.generated.resources.core_ui_quantity
import androidclient.core.ui.generated.resources.core_ui_savings_product
import androidclient.core.ui.generated.resources.core_ui_settlement_date
import androidclient.core.ui.generated.resources.core_ui_start_date
import androidclient.core.ui.generated.resources.core_ui_status
import androidclient.core.ui.generated.resources.core_ui_total_collateral_value
import androidclient.core.ui.generated.resources.core_ui_total_value
import androidclient.core.ui.generated.resources.core_ui_transfer_external_id
import androidclient.core.ui.generated.resources.core_ui_type
import androidclient.core.ui.generated.resources.core_ui_waived
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosListingComponentOutline(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignToken.padding.extraExtraSmall)
            .border(
                width = 1.dp,
                shape = DesignToken.shapes.medium,
                color = color,
            )
            .padding(DesignToken.padding.large),
    ) {
        content()
    }
}

@Composable
fun MifosListingRowItem(
    keyContent: @Composable () -> Unit,
    valueContent: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        keyContent()
        valueContent()
    }
}

@Composable
fun MifosListingColumnItem(
    keyContent: @Composable () -> Unit,
    valueContent: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        keyContent()
        valueContent()
    }
}

@Composable
fun MifosListingColumnItem(
    key: String,
    value: String,
    keyStyle: TextStyle = MifosTypography.labelSmall,
    keyColor: Color = MaterialTheme.colorScheme.secondary,
    valueStyle: TextStyle = MifosTypography.bodySmall,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    MifosListingColumnItem(
        keyContent = {
            Text(
                text = key,
                style = keyStyle.copy(color = keyColor),
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        },
        valueContent = {
            Text(
                text = value,
                style = valueStyle.copy(color = valueColor),
                overflow = TextOverflow.Clip,
                maxLines = 1,
            )
        },
    )
}

@Composable
fun MifosListingRowItem(
    key: String,
    value: String,
    keyStyle: TextStyle = MifosTypography.labelMediumEmphasized,
    valueStyle: TextStyle = MifosTypography.labelMedium,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    MifosListingRowItem(
        keyContent = {
            if (key.isNotBlank()) {
                Text(
                    text = "$key:",
                    style = keyStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        },
        valueContent = {
            Text(
                text = value,
                style = valueStyle.copy(color = valueColor),
                overflow = TextOverflow.Clip,
                maxLines = 1,
            )
        },
    )
}

@Composable
fun MifosListingRowItemHeader(
    text: String,
    keyStyle: TextStyle = MifosTypography.labelMediumEmphasized,
) {
    MifosListingRowItem(
        keyContent = {
            if (text.isNotBlank()) {
                Text(text = text, style = keyStyle)
            }
        },
        valueContent = {
            Icon(
                imageVector = MifosIcons.MoreHoriz,
                contentDescription = "More vert",
            )
        },
    )
}

@Composable
fun MifosDefaultListingComponent(
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    data: Map<String, String>,
) {
    MifosListingComponentOutline(
        color = color,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
        ) {
            data.forEach { (key, value) ->
                MifosListingRowItem(
                    key = key,
                    value = value,
                )
            }
        }
    }
}

@Composable
fun MifosDefaultListingComponentFromStringResources(
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
    data: Map<StringResource, String>,
) {
    MifosListingComponentOutline(
        color = color,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
        ) {
            data.forEach { (key, value) ->
                MifosListingRowItem(
                    key = stringResource(key),
                    value = value,
                )
            }
        }
    }
}

@Composable
fun MifosClientFeeListingComponent(
    name: String,
    dueAsOf: String,
    due: String,
    paid: String,
    waived: String,
    outstanding: String,
) {
    MifosListingComponentOutline {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_name),
                value = name,
                keyStyle = MifosTypography.titleSmallEmphasized,
                valueStyle = MifosTypography.titleSmall,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_due_as_of),
                value = dueAsOf,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_due),
                    value = due,
                    valueColor = MaterialTheme.colorScheme.error,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_paid),
                    value = paid,
                    valueColor = AppColors.customEnable,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_waived),
                    value = waived,
                    valueColor = AppColors.customYellow,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_outstanding),
                value = outstanding,
            )
        }
    }
}

@Composable
fun MifosLoanListingComponent(
    accountNo: String,
    loanProduct: String,
    originalLoan: String,
    amountPaid: String,
    loanBalance: String,
    type: String,
) {
    MifosListingComponentOutline {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_account_no),
                value = accountNo,
                keyStyle = MifosTypography.titleSmallEmphasized,
                valueStyle = MifosTypography.titleSmall,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_loan_product),
                value = loanProduct,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_original_loan),
                    value = originalLoan,
                    valueColor = MaterialTheme.colorScheme.primary,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_amount_paid),
                    value = amountPaid,
                    valueColor = AppColors.customEnable,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_loan_balance),
                    value = loanBalance,
                    valueColor = AppColors.customYellow,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_type),
                value = type,
            )
        }
    }
}

@Composable
fun MifosSavingsListingComponent(
    accountNo: String,
    savingsProduct: String,
    lastActive: String,
    balance: String,
) {
    MifosListingComponentOutline {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_account_no),
                value = accountNo,
                keyStyle = MifosTypography.titleSmallEmphasized,
                valueStyle = MifosTypography.titleSmall,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_savings_product),
                value = savingsProduct,
            )
            Spacer(Modifier.height(DesignToken.padding.medium))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_last_active),
                    value = lastActive,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_balance),
                    value = balance,
                    valueColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
fun MifosCollateralDataListingComponent(
    name: String,
    quantity: String,
    totalValue: String,
    totalCollateralValue: String,
) {
    MifosListingComponentOutline {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            MifosListingRowItem(
                key = "Name",
                value = name,
                keyStyle = MifosTypography.titleSmallEmphasized,
                valueStyle = MifosTypography.titleSmall,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_quantity),
                    value = quantity,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_total_value),
                    value = totalValue,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_total_collateral_value),
                value = totalCollateralValue,
                valueColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun MifosIdentifierListingComponent(
    type: String,
    id: String,
    key: String,
    status: Status,
    description: String,
    identifyDocuments: String,
) {
    MifosListingComponentOutline {
        Column {
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_document_type),
                value = type,
                keyStyle = MifosTypography.titleSmallEmphasized,
                valueStyle = MifosTypography.titleSmall,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_document_id),
                    value = id,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_document_key),
                    value = key,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_status),
                    value = status.name,
                    valueColor = status.color,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_description),
                    value = description,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_identify_documents),
                    value = identifyDocuments,
                )
            }
        }
    }
}

enum class Status(val color: Color) {
    Inactive(AppColors.lightRed),
    Pending(AppColors.customYellow),
    Active(AppColors.customEnable),
}

@Composable
fun MifosDelinquencyListingComponent(
    date: String,
    time: String,
    isPause: Boolean,
    startDate: String,
    endDate: String,
) {
    MifosListingComponentOutline {
        Column {
            MifosListingRowItem(
                value = "$date; $time",
                key = "",
                valueStyle = MifosTypography.titleSmallEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_action),
                    value = if (isPause) "Pause" else "Resume",
                    valueColor = if (isPause) AppColors.customYellow else AppColors.customEnable,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_start_date),
                    value = startDate,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_end_date),
                    value = endDate,
                )
            }
        }
    }
}

@Composable
fun MifosChargesListingComponent(
    title: String,
    type: String,
    dueAsOf: String,
    calculationType: String,
    due: String,
    paid: String,
    waived: String,
    outstanding: String,
) {
    MifosListingComponentOutline {
        Column {
            MifosListingRowItem(
                key = "",
                value = title,
                valueStyle = MifosTypography.titleSmallEmphasized,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_type),
                    value = type,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_due_as_of),
                    value = dueAsOf,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_calculation_type),
                    value = calculationType,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.medium))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_due),
                    value = due,
                    valueColor = AppColors.lightRed,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_paid),
                    value = paid,
                    valueColor = AppColors.customEnable,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_waived),
                    value = waived,
                    valueColor = AppColors.customYellow,
                )
            }
            Spacer(Modifier.height(DesignToken.padding.medium))
            MifosListingRowItem(
                key = stringResource(Res.string.core_ui_outstanding),
                value = outstanding,
            )
        }
    }
}

@Composable
fun MifosSellLoanListingComponent(
    status: Status,
    effectiveForm: String,
    ownerExternalId: String,
    transferExternalId: String,
    settlementDate: String,
    purchasePriceRatio: String,
) {
    MifosListingComponentOutline {
        Column {
            MifosListingRowItem(
                value = when (status) {
                    Status.Inactive -> stringResource(Res.string.core_ui_cancelled)
                    Status.Pending -> stringResource(Res.string.core_ui_pending)
                    Status.Active -> stringResource(Res.string.core_ui_completed)
                },
                key = "",
                valueStyle = MifosTypography.titleSmallEmphasized,
                valueColor = status.color,
            )
            Spacer(Modifier.height(DesignToken.padding.large))
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignToken.padding.medium),
            ) {
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_effective_from),
                    value = effectiveForm,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_owner_external_id),
                    value = ownerExternalId,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_transfer_external_id),
                    value = transferExternalId,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_settlement_date),
                    value = settlementDate,
                )
                MifosListingRowItem(
                    key = stringResource(Res.string.core_ui_purchase_price_ratio),
                    value = purchasePriceRatio,
                )
            }
        }
    }
}

@Preview
@Composable
private fun MifosDefaultListingComponentPreview() {
    val sampleData = mapOf(
        "Name" to "John Doe",
        "Email" to "john.doe@example.com",
        "Role" to "Developer",
        "Location" to "Remote",
    )

    MifosTheme {
        MifosDefaultListingComponent(data = sampleData)
    }
}

@Preview
@Composable
private fun PreviewMifosClientFeeListingComponent() {
    MaterialTheme {
        MifosClientFeeListingComponent(
            name = "John Doe",
            dueAsOf = "2025-08-19",
            due = "$1000",
            paid = "$400",
            waived = "$50",
            outstanding = "$550",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosLoanListingComponent() {
    MaterialTheme {
        MifosLoanListingComponent(
            accountNo = "LN12345",
            loanProduct = "Personal Loan",
            originalLoan = "$5000",
            amountPaid = "$2000",
            loanBalance = "$3000",
            type = "Active",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosSavingsListingComponent() {
    MaterialTheme {
        MifosSavingsListingComponent(
            accountNo = "SV9876",
            savingsProduct = "Regular Savings",
            lastActive = "2025-08-15",
            balance = "$1200",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosCollateralDataListingComponent() {
    MaterialTheme {
        MifosCollateralDataListingComponent(
            name = "Gold Jewelry",
            quantity = "5",
            totalValue = "$2500",
            totalCollateralValue = "$2500",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosIdentifierListingComponent() {
    MaterialTheme {
        MifosIdentifierListingComponent(
            type = "Passport",
            id = "P1234567",
            key = "Government ID",
            status = Status.Active,
            description = "Primary identification document",
            identifyDocuments = "Passport Scan",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosDelinquencyListingComponent() {
    MaterialTheme {
        MifosDelinquencyListingComponent(
            date = "2025-08-01",
            time = "10:00 AM",
            isPause = false,
            startDate = "2025-07-01",
            endDate = "2025-07-31",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosChargesListingComponent() {
    MaterialTheme {
        MifosChargesListingComponent(
            title = "Processing Fee",
            type = "Flat",
            dueAsOf = "2025-08-19",
            calculationType = "Fixed",
            due = "$100",
            paid = "$50",
            waived = "$10",
            outstanding = "$40",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosSellLoanListingComponent() {
    MaterialTheme {
        MifosSellLoanListingComponent(
            status = Status.Pending,
            effectiveForm = "2025-08-20",
            ownerExternalId = "OWN123",
            transferExternalId = "TRF456",
            settlementDate = "2025-08-25",
            purchasePriceRatio = "0.85",
        )
    }
}

@Preview
@Composable
private fun PreviewMifosHeader() {
    MaterialTheme {
        MifosListingRowItemHeader(
            text = "Hello",
        )
    }
}
