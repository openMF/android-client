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
import androidclient.core.ui.generated.resources.client_share_accounts_approved_shares
import androidclient.core.ui.generated.resources.client_share_accounts_pending_for_approval_shares
import androidclient.core.ui.generated.resources.client_share_accounts_share_product
import androidclient.core.ui.generated.resources.core_ui_account_no
import androidclient.core.ui.generated.resources.core_ui_amount_paid
import androidclient.core.ui.generated.resources.core_ui_balance
import androidclient.core.ui.generated.resources.core_ui_description
import androidclient.core.ui.generated.resources.core_ui_document_id
import androidclient.core.ui.generated.resources.core_ui_document_key
import androidclient.core.ui.generated.resources.core_ui_due
import androidclient.core.ui.generated.resources.core_ui_due_as_of
import androidclient.core.ui.generated.resources.core_ui_identify_documents
import androidclient.core.ui.generated.resources.core_ui_last_active
import androidclient.core.ui.generated.resources.core_ui_loan_balance
import androidclient.core.ui.generated.resources.core_ui_loan_product
import androidclient.core.ui.generated.resources.core_ui_note_createdBy
import androidclient.core.ui.generated.resources.core_ui_note_date
import androidclient.core.ui.generated.resources.core_ui_note_note
import androidclient.core.ui.generated.resources.core_ui_original_loan
import androidclient.core.ui.generated.resources.core_ui_outstanding
import androidclient.core.ui.generated.resources.core_ui_paid
import androidclient.core.ui.generated.resources.core_ui_quantity
import androidclient.core.ui.generated.resources.core_ui_status
import androidclient.core.ui.generated.resources.core_ui_total_collateral_value
import androidclient.core.ui.generated.resources.core_ui_total_value
import androidclient.core.ui.generated.resources.core_ui_type
import androidclient.core.ui.generated.resources.core_ui_waived
import androidclient.core.ui.generated.resources.listing_component_identifier_not_available
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.designsystem.utils.onClick
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosActionsListingComponentOutline(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    content: @Composable () -> Unit,
) {
    val bottomCorner by animateDpAsState(
        if (isExpanded) 0.dp else 12.dp,
        animationSpec = spring(),
    )

    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomStart = bottomCorner,
                    bottomEnd = bottomCorner,
                ),
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
    status: Status?,
    description: String,
    identifyDocuments: String,
    isExpanded: Boolean,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current

    Column {
        MifosActionsListingComponentOutline(
            isExpanded = isExpanded,
        ) {
            Column(modifier = Modifier.padding(DesignToken.padding.large).onClick { onClick() }) {
                MifosListingRowItemHeader(
                    text = type,
                    keyStyle = MifosTypography.titleSmallEmphasized,
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
                        value = status?.name
                            ?: stringResource(Res.string.listing_component_identifier_not_available),
                        valueColor = if (status?.name != null) status.color else Color.Red,
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
        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(DesignToken.sizes.avatarMedium)
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
                                text = menuItem::class.simpleName ?: "",
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
                            modifier = Modifier.fillMaxWidth()
                                .height(DesignToken.sizes.avatarMedium)
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
                                text = menuItem::class.simpleName ?: "",
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
    var isActive by rememberSaveable { mutableStateOf(false) }

    Column {
        MifosActionsListingComponentOutline(
            isExpanded = isActive,
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large)
                    .onClick { isActive = !isActive },
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
        if (isActive) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(DesignToken.sizes.avatarMedium)
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
                                text = menuItem::class.simpleName ?: "",
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
fun MifosActionsShareListingComponent(
    accountNo: String,
    shareProductName: String,
    pendingForApprovalShares: Int?,
    approvedShares: Int?,
    isExpanded: Boolean,
    onClick: () -> Unit,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline {
        Column(
            modifier = Modifier.clickable { onClick() },
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
            ) {
                MifosListingRowItemHeader(
                    text = accountNo,
                    keyStyle = MifosTypography.titleSmallEmphasized,
                )

                Spacer(Modifier.height(DesignToken.padding.large))
                MifosListingRowItem(
                    key = stringResource(Res.string.client_share_accounts_share_product),
                    value = shareProductName,
                )
                Spacer(Modifier.height(DesignToken.padding.medium))
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignToken.padding.extraExtraSmall),
                ) {
                    MifosListingRowItem(
                        key = stringResource(Res.string.client_share_accounts_pending_for_approval_shares),
                        value = (
                            pendingForApprovalShares
                                ?: stringResource(Res.string.listing_component_identifier_not_available)
                            ).toString(),
                    )
                    MifosListingRowItem(
                        key = stringResource(Res.string.client_share_accounts_approved_shares),
                        value = (
                            approvedShares
                                ?: stringResource(Res.string.listing_component_identifier_not_available)
                            ).toString(),
                    )
                }
            }

            if (isExpanded) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(
                        bottomStart = DesignToken.padding.medium,
                        bottomEnd = DesignToken.padding.medium,
                    ),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(
                            vertical = DesignToken.padding.small,
                        ),
                    ) {
                        menuList.map { menuItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .height(DesignToken.sizes.avatarMedium)
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
                                    text = menuItem::class.simpleName ?: "",
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
}

@Composable
fun MifosActionsSavingsListingComponent(
    accountNo: String,
    savingsProduct: String,
    savingsProductName: String,
    lastActive: String,
    balance: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column {
        MifosActionsListingComponentOutline(
            isExpanded = isExpanded,
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
            ) {
                MifosListingRowItemHeader(
                    text = accountNo,
                    keyStyle = MifosTypography.titleSmallEmphasized,
                )

                Spacer(Modifier.height(DesignToken.padding.large))
                MifosListingRowItem(
                    key = savingsProduct,
                    value = savingsProductName,
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

        if (isExpanded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(DesignToken.sizes.avatarMedium)
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
                                text = menuItem::class.simpleName ?: "",
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
    isActive: Boolean,
    onClick: () -> Unit,
    outstanding: String,
    menuList: List<Actions>,
    onActionClicked: (Actions) -> Unit,
) {
    MifosActionsListingComponentOutline(isExpanded = isActive) {
        Column {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large)
                    .clickable { onClick() },
            ) {
                MifosListingRowItemHeader(
                    text = name,
                    keyStyle = MifosTypography.titleSmallEmphasized,
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
            if (isActive) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(
                        bottomStart = DesignToken.padding.medium,
                        bottomEnd = DesignToken.padding.medium,
                    ),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(
                            vertical = DesignToken.padding.small,
                        ),
                    ) {
                        menuList.map { menuItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .height(DesignToken.sizes.avatarMedium)
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
                                    text = menuItem::class.simpleName ?: "",
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
}

sealed class Actions(open val icon: ImageVector) {
    data class ViewAccount(override val icon: ImageVector = MifosIcons.PiggyBank) : Actions(icon)
    data class ApproveAccount(override val icon: ImageVector = MifosIcons.ApproveAccount) :
        Actions(icon)

    data class PayOutstandingAmount(override val icon: ImageVector = MifosIcons.MakeRepayment) :
        Actions(icon)

    data class MakeRepayment(override val icon: ImageVector = MifosIcons.MakeRepayment) :
        Actions(icon)

    data class ViewDocument(override val icon: ImageVector = MifosIcons.DocumentScanner) :
        Actions(icon)

    data class UploadAgain(override val icon: ImageVector = MifosIcons.FileUpload) : Actions(icon)
    data class DeleteDocument(override val icon: ImageVector = MifosIcons.DeleteDocument) :
        Actions(icon)

    data class Edit(override val icon: ImageVector = MifosIcons.Edit) : Actions(icon)
    data class Delete(override val icon: ImageVector = MifosIcons.Delete) : Actions(icon)
}

@Composable
fun MifosActionsNoteListingComponent(
    createdBy: String,
    date: String,
    notes: String,
    menuList: List<Actions>,
    onExpand: () -> Unit,
    isExpanded: Boolean,
    onActionClicked: (Actions) -> Unit,
) {
    val density = LocalDensity.current

    Column {
        MifosActionsListingComponentOutline(
            modifier = Modifier.clickable { onExpand() },
            isExpanded = isExpanded,
        ) {
            Column(
                modifier = Modifier.padding(DesignToken.padding.large),
            ) {
                MifosListingRowItemHeader(
                    text = stringResource(Res.string.core_ui_note_createdBy) + " " + createdBy,
                    keyStyle = MifosTypography.titleSmallEmphasized,
                )

                Spacer(Modifier.height(DesignToken.padding.large))

                MifosListingColumnItem(
                    key = stringResource(Res.string.core_ui_note_date),
                    value = date,
                )

                Spacer(Modifier.height(DesignToken.padding.medium))

                MifosListingColumnItem(
                    key = stringResource(Res.string.core_ui_note_note),
                    value = notes,
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top,
            ) + fadeIn(
                initialAlpha = 0.3f,
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = DesignToken.padding.medium,
                    bottomEnd = DesignToken.padding.medium,
                ),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = DesignToken.padding.small,
                    ),
                ) {
                    menuList.map { menuItem ->
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .height(DesignToken.sizes.avatarMedium)
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
                                text = menuItem::class.simpleName ?: "",
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

@Preview
@Composable
private fun PreviewMifosActionsNoteListingComponent() {
    MaterialTheme {
        MifosActionsNoteListingComponent(
            createdBy = "John Doe",
            date = "02 September 2025",
            notes = "This is a note",
            menuList = listOf(
                Actions.Edit(),
                Actions.Delete(),
            ),
            onExpand = {},
            isExpanded = true,
            onActionClicked = { action ->
                when (action) {
                    is Actions.Edit -> println(Actions.ViewAccount::class.simpleName ?: "")
                    is Actions.Delete -> println(Actions.ApproveAccount::class.simpleName ?: "")
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewMifosActionsIdentifierListingComponent() {
    MaterialTheme {
        MifosActionsIdentifierListingComponent(
            type = "Passport",
            id = "P1234567",
            key = "Government ID",
            status = Status.Active,
            description = "Primary identification document",
            identifyDocuments = "Passport Scan",
            menuList = listOf(
                Actions.ViewDocument(),
                Actions.UploadAgain(),
                Actions.DeleteDocument(),
            ),
            onActionClicked = { action ->
                when (action) {
                    is Actions.ViewDocument -> println(Actions.ViewDocument::class.simpleName)
                    is Actions.UploadAgain -> println(Actions.UploadAgain::class.simpleName)
                    is Actions.DeleteDocument -> println(Actions.DeleteDocument::class.simpleName)
                    else -> println("Action not Handled")
                }
            },
            onClick = {},
            isExpanded = true,
        )
    }
}

@Preview
@Composable
private fun PreviewMifosActionsClientFeeListingComponent() {
    MaterialTheme {
        MifosActionsClientFeeListingComponent(
            name = "John Doe",
            dueAsOf = "2025-08-19",
            due = "$1000",
            paid = "$400",
            waived = "$50",
            outstanding = "$550",
            menuList = listOf(
                Actions.ViewAccount(),
                Actions.ApproveAccount(),
            ),
            isActive = true,
            onClick = {},
            onActionClicked = { action ->
                when (action) {
                    is Actions.ViewAccount -> println(Actions.ViewDocument::class.simpleName)
                    is Actions.ApproveAccount -> println(Actions.ApproveAccount::class.simpleName)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewMifosActionsSavingsListingComponent() {
    MaterialTheme {
        MifosActionsSavingsListingComponent(
            accountNo = "SV9876",
            savingsProduct = "Savings Product",
            savingsProductName = "Wallet",
            lastActive = "2025-08-15",
            balance = "$1200",
            menuList = listOf(
                Actions.ViewAccount(),
                Actions.ApproveAccount(),
            ),
            onActionClicked = { action ->
                when (action) {
                    is Actions.ViewAccount -> println(Actions.ViewDocument::class.simpleName)
                    is Actions.ApproveAccount -> println(Actions.ApproveAccount::class.simpleName)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewMifosActionsCollateralDataListingComponent() {
    MaterialTheme {
        MifosActionsCollateralDataListingComponent(
            name = "Gold Jewelry",
            quantity = "5",
            totalValue = "$2500",
            totalCollateralValue = "$2500",
            menuList = listOf(
                Actions.ViewAccount(),
                Actions.ApproveAccount(),
            ),
            onActionClicked = { action ->
                when (action) {
                    is Actions.ViewAccount -> println(Actions.ViewDocument::class.simpleName)
                    is Actions.ApproveAccount -> println(Actions.ApproveAccount::class.simpleName)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewMifosActionsLoanListingComponent() {
    MaterialTheme {
        MifosActionsLoanListingComponent(
            accountNo = "LN12345",
            loanProduct = "Personal Loan",
            originalLoan = "$5000",
            amountPaid = "$2000",
            loanBalance = "$3000",
            type = "Active",
            menuList = listOf(
                Actions.ViewAccount(),
                Actions.ApproveAccount(),
            ),
            onActionClicked = { action ->
                when (action) {
                    is Actions.ViewAccount -> println(Actions.ViewAccount::class.simpleName)
                    is Actions.ApproveAccount -> println(Actions.ApproveAccount::class.simpleName)
                    else -> println("Action not Handled")
                }
            },
        )
    }
}
