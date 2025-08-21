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
import androidclient.core.ui.generated.resources.core_ui_amount_paid
import androidclient.core.ui.generated.resources.core_ui_balance
import androidclient.core.ui.generated.resources.core_ui_description
import androidclient.core.ui.generated.resources.core_ui_document_id
import androidclient.core.ui.generated.resources.core_ui_document_key
import androidclient.core.ui.generated.resources.core_ui_document_type
import androidclient.core.ui.generated.resources.core_ui_due
import androidclient.core.ui.generated.resources.core_ui_due_as_of
import androidclient.core.ui.generated.resources.core_ui_identify_documents
import androidclient.core.ui.generated.resources.core_ui_last_active
import androidclient.core.ui.generated.resources.core_ui_loan_balance
import androidclient.core.ui.generated.resources.core_ui_loan_product
import androidclient.core.ui.generated.resources.core_ui_name
import androidclient.core.ui.generated.resources.core_ui_original_loan
import androidclient.core.ui.generated.resources.core_ui_outstanding
import androidclient.core.ui.generated.resources.core_ui_paid
import androidclient.core.ui.generated.resources.core_ui_quantity
import androidclient.core.ui.generated.resources.core_ui_savings_product
import androidclient.core.ui.generated.resources.core_ui_status
import androidclient.core.ui.generated.resources.core_ui_total_collateral_value
import androidclient.core.ui.generated.resources.core_ui_total_value
import androidclient.core.ui.generated.resources.core_ui_type
import androidclient.core.ui.generated.resources.core_ui_waived
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosActionsListingComponentOutline(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = DesignToken.shapes.medium,
                color = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        content()
    }
}

@Composable
fun MifosActionsIdentifierListingComponent(
    type: String,
    id: String,
    key: String,
    status: Status,
    description: String,
    identifyDocuments: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column {
            Column(modifier = Modifier.padding(DesignToken.padding.large)) {
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.avatarMedium)
                                .clickable {
                                    onActionClicked(menuItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
                                imageVector = menuItem.icon,
                                contentDescription = "",
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MifosActionsCollateralDataListingComponent(
    name: String,
    quantity: String,
    totalValue: String,
    totalCollateralValue: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.avatarMedium)
                                .clickable {
                                    onActionClicked(menuItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
                                imageVector = menuItem.icon,
                                contentDescription = "",
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MifosActionsLoanListingComponent(
    accountNo: String,
    loanProduct: String,
    originalLoan: String,
    amountPaid: String,
    loanBalance: String,
    type: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.avatarMedium)
                                .clickable {
                                    onActionClicked(menuItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
                                imageVector = menuItem.icon,
                                contentDescription = "",
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MifosActionsSavingsListingComponent(
    accountNo: String,
    savingsProduct: String,
    lastActive: String,
    balance: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.avatarMedium)
                                .clickable {
                                    onActionClicked(menuItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
                                imageVector = menuItem.icon,
                                contentDescription = "",
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MifosActionsClientFeeListingComponent(
    name: String,
    dueAsOf: String,
    due: String,
    paid: String,
    waived: String,
    outstanding: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth().height(DesignToken.sizes.avatarMedium)
                                .clickable {
                                    onActionClicked(menuItem)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,
                        ) {
                            Icon(
                                modifier = Modifier.padding(horizontal = DesignToken.padding.large),
                                imageVector = menuItem.icon,
                                contentDescription = "",
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = menuItem.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class Actions(val icon: ImageVector) {
    ViewAccount(MifosIcons.ViewAccount),
    ApproveAccount(MifosIcons.ApproveAccount),
    ViewDocument(MifosIcons.ViewDocument),
    UploadAgain(MifosIcons.UploadAgain),
    DeleteDocument(MifosIcons.DeleteDocument),
}

@Preview
@Composable
fun PreviewMifosActionsIdentifierListingComponent() {
    MaterialTheme {
        MifosActionsIdentifierListingComponent(
            type = "Passport",
            id = "P1234567",
            key = "Government ID",
            status = Status.Active,
            description = "Primary identification document",
            identifyDocuments = "Passport Scan",
            menuList = listOf(
                Actions.ViewDocument,
                Actions.UploadAgain,
                Actions.DeleteDocument,
            ),
            onActionClicked = { action ->
                when (action) {
                    Actions.ViewDocument -> println(Actions.ViewDocument.name)
                    Actions.UploadAgain -> println(Actions.UploadAgain.name)
                    Actions.DeleteDocument -> println(Actions.DeleteDocument.name)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewMifosActionsClientFeeListingComponent() {
    MaterialTheme {
        MifosActionsClientFeeListingComponent(
            name = "John Doe",
            dueAsOf = "2025-08-19",
            due = "$1000",
            paid = "$400",
            waived = "$50",
            outstanding = "$550",
            menuList = listOf(
                Actions.ViewAccount,
                Actions.ApproveAccount,
            ),
            onActionClicked = { action ->
                when (action) {
                    Actions.ViewAccount -> println(Actions.ViewAccount.name)
                    Actions.ApproveAccount -> println(Actions.ApproveAccount.name)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewMifosActionsSavingsListingComponent() {
    MaterialTheme {
        MifosActionsSavingsListingComponent(
            accountNo = "SV9876",
            savingsProduct = "Regular Savings",
            lastActive = "2025-08-15",
            balance = "$1200",
            menuList = listOf(
                Actions.ViewAccount,
                Actions.ApproveAccount,
            ),
            onActionClicked = { action ->
                when (action) {
                    Actions.ViewAccount -> println(Actions.ViewAccount.name)
                    Actions.ApproveAccount -> println(Actions.ApproveAccount.name)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewMifosActionsCollateralDataListingComponent() {
    MaterialTheme {
        MifosActionsCollateralDataListingComponent(
            name = "Gold Jewelry",
            quantity = "5",
            totalValue = "$2500",
            totalCollateralValue = "$2500",
            menuList = listOf(
                Actions.ViewAccount,
                Actions.ApproveAccount,
            ),
            onActionClicked = { action ->
                when (action) {
                    Actions.ViewAccount -> println(Actions.ViewAccount.name)
                    Actions.ApproveAccount -> println(Actions.ApproveAccount.name)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
fun PreviewMifosActionsLoanListingComponent() {
    MaterialTheme {
        MifosActionsLoanListingComponent(
            accountNo = "LN12345",
            loanProduct = "Personal Loan",
            originalLoan = "$5000",
            amountPaid = "$2000",
            loanBalance = "$3000",
            type = "Active",
            menuList = listOf(
                Actions.ViewAccount,
                Actions.ApproveAccount,
            ),
            onActionClicked = { action ->
                when (action) {
                    Actions.ViewAccount -> println(Actions.ViewAccount.name)
                    Actions.ApproveAccount -> println(Actions.ApproveAccount.name)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}
