/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.AppTheme
import kpt.core.designsystem.theme.AppTypography
import kpt.core.designsystem.theme.DesignToken
import kpt.core.ui.generated.resources.Res
import kpt.core.ui.generated.resources.core_ui_core_common_working
import kpt.core.ui.generated.resources.core_ui_ic_centers_24dp
import kpt.core.ui.generated.resources.core_ui_ic_group_black_24dp
import kpt.core.ui.generated.resources.core_ui_no_internet
import kpt.core.ui.util.DevicePreview
import kpt.core.ui.util.TextUtil
import org.jetbrains.compose.ui.tooling.preview.Preview

@DevicePreview
@Composable
private fun EmptyDataViewPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        EmptyDataView(
            error = Res.string.core_ui_no_internet,
            modifier = modifier,
            image = null,
        )
    }
}

@DevicePreview
@Composable
private fun EmptyDataViewPreviewForIcon(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        EmptyDataView(
            error = "This is error",
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun PreviewAppActionsNoteListingComponent() {
    KptTheme {
        AppActionsNoteListingComponent(
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
private fun PreviewAppActionsIdentifierListingComponent() {
    KptTheme {
        AppActionsIdentifierListingComponent(
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
private fun PreviewAppActionsClientFeeListingComponent() {
    KptTheme {
        AppActionsClientFeeListingComponent(
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
private fun PreviewAppActionsSavingsListingComponent() {
    KptTheme {
        AppActionsSavingsListingComponent(
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
            isExpanded = true,
            onExpandToggle = {},
        )
    }
}

@Preview
@Composable
private fun PreviewAppActionsCollateralDataListingComponent() {
    KptTheme {
        AppActionsCollateralDataListingComponent(
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
private fun PreviewAppActionsLoanListingComponent() {
    KptTheme {
        AppActionsLoanListingComponent(
            accountNo = "LN12345",
            loanProduct = "Personal Loan",
            originalLoan = "$5000",
            amountPaid = "$2000",
            loanBalance = "$3000",
            type = "Active",
            status = "Active",
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

@Preview
@Composable
private fun AppAddressCardPreview() {
    val sampleAddresses = mapOf(
        "Address 1" to "123, MG Road",
        "Address 2" to "2nd Floor, Lotus Building",
        "City" to "Bengaluru",
        "State" to "Karnataka",
        "Pincode" to "560001",
        "Country" to "India",
    )

    AppTheme {
        AppAddressCard(
            title = "Client Address",
            addressList = sampleAddresses,
        )
    }
}

@Preview
@Composable
private fun AppAlertDialogPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppAlertDialog(
            dialogTitle = "Dialog Title",
            dialogText = "Dialog Text",
            dismissText = "Dismiss",
            confirmationText = "Confirm",
            onDismissRequest = {},
            onConfirmation = {},
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun BreadcrumbItemPreview() {
    AppTheme {
        Row(
            modifier = Modifier.padding(DesignToken.padding.medium),
        ) {
            BreadcrumbItem(
                text = "Home",
                isActive = false,
                onClick = {},
            )
            Text(" > ")
            BreadcrumbItem(
                text = "Loan Account",
                isActive = true,
                onClick = {},
            )
        }
    }
}

@Preview
@Composable
private fun AppCheckBoxPreview() {
    AppTheme {
        AppCheckBox(
            checked = false,
            onCheckChanged = {},
            text = "",
        )
    }
}

@Preview
@Composable
private fun AppEmptyCardPreview() {
    AppTheme {
        AppEmptyCard("Add any to show")
    }
}

@Preview
@Composable
private fun AppEmptyUiPreview() {
    AppTheme {
        AppEmptyUi(
            text = "No data available",
            icon = AppIcons.Info,
        )
    }
}

@Preview
@Composable
private fun NoInternetPreview() {
    AppTheme {
        NoInternetComponent()
    }
}

@Preview
@Composable
private fun EmptyDataPreview() {
    AppTheme {
        EmptyDataComponent()
    }
}

@Preview
@Composable
private fun EmptyDataComponentWithModifiedMessageAndIconPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        EmptyDataComponentWithModifiedMessageAndIcon(
            message = "No data found",
            icon = AppIcons.Error,
            modifier = modifier,
            isEmptyData = true,
        )
    }
}

@Preview
@Composable
private fun AppFABPreview() {
    AppTheme {
        AppFAB(
            icon = AppIcons.Info,
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun AppMobileIconPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppIcon(
            mobileIcon = Res.drawable.core_ui_ic_centers_24dp,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun AppItemCardPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppItemCard(
            onClick = {},
            modifier = modifier,
        ) {
            Text(text = "Card Content")
        }
    }
}

@Preview
@Composable
private fun AppLinkTextPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppLinkText(
            text = "Link Text",
            onClick = {},
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun AppDefaultListingComponentPreview() {
    val sampleData = mapOf(
        "Name" to "John Doe",
        "Email" to "john.doe@example.com",
        "Role" to "Developer",
        "Location" to "Remote",
    )

    AppTheme {
        AppDefaultListingComponent(data = sampleData)
    }
}

@Preview
@Composable
private fun PreviewAppClientFeeListingComponent() {
    KptTheme {
        AppClientFeeListingComponent(
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
private fun PreviewAppLoanListingComponent() {
    KptTheme {
        AppLoanListingComponent(
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
private fun PreviewAppSavingsListingComponent() {
    KptTheme {
        AppSavingsListingComponent(
            accountNo = "SV9876",
            savingsProduct = "Regular Savings",
            lastActive = "2025-08-15",
            balance = "$1200",
        )
    }
}

@Preview
@Composable
private fun PreviewAppCollateralDataListingComponent() {
    KptTheme {
        AppCollateralDataListingComponent(
            name = "Gold Jewelry",
            quantity = "5",
            totalValue = "$2500",
            totalCollateralValue = "$2500",
        )
    }
}

@Preview
@Composable
private fun PreviewAppIdentifierListingComponent() {
    KptTheme {
        AppIdentifierListingComponent(
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
private fun PreviewAppDelinquencyListingComponent() {
    KptTheme {
        AppDelinquencyListingComponent(
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
private fun PreviewAppChargesListingComponent() {
    KptTheme {
        AppChargesListingComponent(
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
private fun PreviewAppSellLoanListingComponent() {
    KptTheme {
        AppSellLoanListingComponent(
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
private fun PreviewAppHeader() {
    KptTheme {
        AppListingRowItemHeader(
            text = "Hello",
        )
    }
}

@Preview
@Composable
private fun AppProgressIndicatorPreview() {
    AppTheme {
        AppProgressIndicator()
    }
}

@Preview
@Composable
private fun AppProgressIndicatorOverlayPreview() {
    AppTheme {
        AppProgressIndicatorOverlay()
    }
}

@Preview
@Composable
private fun PreviewRadioButtonDialog() {
    AppTheme {
        AppRadioButtonDialog(
            titleResId = Res.string.core_ui_core_common_working,
            items = arrayOf("1", "2", "3"),
            selectedItem = "1",
            onDismissRequest = { },
            selectItem = { _, _ -> },
        )
    }
}

@DevicePreview
@Composable
private fun AppRoundIconPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppRoundIcon(
            iconId = Res.drawable.core_ui_ic_group_black_24dp,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun AppRowTextWithButton() {
    AppTheme {
        AppRowTextWithButton(
            title = "Title",
            description = "Description",
            size = "Size",
            btnText = "Button",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewAppRowCard() {
    AppTheme {
        AppRowCard(
            title = "Customer Info",
            leftValues = listOf(
                TextUtil(text = "Name", style = AppTypography.labelMediumEmphasized),
                TextUtil(text = "Age", style = AppTypography.labelMedium),
                TextUtil(text = "City", style = AppTypography.labelMedium),
            ),
            rightValues = listOf(
                TextUtil(text = "453", style = AppTypography.labelMedium, color = Color.Green),
                TextUtil(text = "455", style = AppTypography.labelMedium),
            ),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewAppRowCardWithImage() {
    AppTheme {
        AppRowCard(
            title = "Customer Info",
            leftValues = listOf(
                TextUtil(text = "Name", style = AppTypography.labelMediumEmphasized),
                TextUtil(text = "Age", style = AppTypography.labelMedium),
                TextUtil(text = "City", style = AppTypography.labelMedium),
            ),
            rightValues = listOf(
                TextUtil(text = "Closed", style = AppTypography.labelMedium, color = Color.Red),
                TextUtil(text = "455", style = AppTypography.labelMedium),
            ),
            byteArray = null,
        )
    }
}

@Preview
@Composable
private fun AppRowWithLabelsPreview() {
    AppTheme {
        AppRowWithLabels(
            title = "Title",
            subTitle = "Sub Title",
            label1 = TextUtil("label1", color = Color.Green),
            label2 = TextUtil("label2"),
            onClick = {},
            byteArray = null,
        )
    }
}

@Preview
@Composable
private fun AppRowDropDownPreview() {
    AppTheme {
        AppDropDownRow(
            isActive = true,
            title = "John Wault",
            accountNo = "Acc No. 838272",
            office = "Head Office",
            date = "20-04-2020",
            onClick = {},
        )
    }
}

@Composable
@Preview
private fun AppRowWithTextAndButtonPreview() {
    AppTheme {
        AppRowWithTextAndButton(
            onBtnClick = {},
            btnText = "View",
            text = "2 Collaterals",
        )
    }
}

@Preview
@Composable
private fun AppSearchBarPreview() {
    var text by remember { mutableStateOf("") }

    AppSearchBar(
        query = text,
        onQueryChange = { text = it },
        onBackClick = { },
        onSearchClick = { println("Searching for $it") },
    )
}

@Composable
@Preview
private fun AppSuccessStatusDialogPreview() {
    AppTheme {
        AppStatusDialog(
            status = ResultStatus.SUCCESS,
            onConfirm = {},
            btnText = "OK",
            successTitle = "Success",
            successMessage = "Operation Successful",
            failureTitle = "Failure",
            failureMessage = "Operation Failed",
        )
    }
}

@Composable
@Preview
private fun AppFailureStatusDialogPreview() {
    AppTheme {
        AppStatusDialog(
            status = ResultStatus.FAILURE,
            onConfirm = {},
            btnText = "OK",
            successTitle = "Success",
            successMessage = "Operation Successful",
            failureTitle = "Failure",
            failureMessage = "Operation Failed",
        )
    }
}

@Preview
@Composable
private fun AppStepperDemo() {
    val steps = listOf(
        Step("Details") { Text("Step 1: Details Content") },
        Step("Terms") { Text("Step 2: Terms Content") },
        Step("Charges") { Text("Step 3: Charges Content") },
        Step("Schedule") { Text("Step 4: Schedule Content") },
        Step("Preview") { Text("Step 5: Preview Content") },
    )

    AppStepper(
        steps = steps,
        currentIndex = 2,
        onStepChange = { },
        modifier = Modifier
            .fillMaxWidth(),
    )
}

@DevicePreview
@Composable
private fun AppTextTitleDescSingleLinePreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppTextTitleDescSingleLine(
            title = "AppTextTitleDescSingleLine Title",
            description = "AppTextTitleDescSingleLine Description",
            modifier = modifier,
        )
    }
}

@DevicePreview
@Composable
private fun AppTextTitleDescDoubleLinePreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppTextTitleDescDoubleLine(
            title = "AppTextTitleDescDoubleLine Title",
            description = "AppTextTitleDescDoubleLine Description",
            descriptionStyle = KptTheme.typography.bodyMedium,
            modifier = modifier,
        )
    }
}

@DevicePreview
@Composable
private fun AppTextTitleDescDrawableSingleLinePreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppTextTitleDescDrawableSingleLine(
            title = "AppTextTitleDescDrawableSingleLine Title",
            description = "AppTextTitleDescDrawableSingleLine Description",
            imageResId = Res.drawable.core_ui_ic_centers_24dp,
            modifier = modifier,
        )
    }
}

@DevicePreview
@Composable
private fun AppTitleDescSingleLineEqualPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppTitleDescSingleLineEqual(
            title = "AppTitleDescSingleLineEqual Title",
            description = "AppTitleDescSingleLineEqual Description",
            modifier = modifier,
        )
    }
}

@DevicePreview
@Composable
private fun AppTextUserImagePreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppTextUserImage(
            text = "A",
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun SelectionModeTopAppBarPreview() {
    AppTheme {
        SelectionModeTopAppBar(
            itemCount = 3,
            resetSelectionMode = {},
        )
    }
}

@Composable
@Preview
private fun AppTwoButtonRowPreview() {
    AppTheme {
        AppTwoButtonRow(
            firstBtnText = "First Button",
            secondBtnText = "Second Button",
            onFirstBtnClick = {},
            onSecondBtnClick = {},
        )
    }
}

@DevicePreview
@Composable
private fun AppUserImagePreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        AppUserImage(
            bitmap = null,
            modifier = modifier,
        )
    }
}

@Preview
@Composable
private fun SelectionModeTopAppBar() {
    AppTheme {
        SelectionModeTopAppBar(
            itemCount = 3,
            resetSelectionMode = {},
        )
    }
}

@Preview
@Composable
private fun SelectionModeTopAppBarWithActionsPreview() {
    AppTheme {
        SelectionModeTopAppBar(
            itemCount = 5,
            resetSelectionMode = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = AppIcons.Delete,
                        contentDescription = "delete",
                    )
                }
            },
        )
    }
}

@DevicePreview
@Composable
private fun NoInternetPreview(
    modifier: Modifier = Modifier,
) {
    AppTheme {
        NoInternet(
            error = Res.string.core_ui_no_internet,
            modifier = modifier,
            isRetryEnabled = true,
            retry = {},
        )
    }
}

@Preview
@Composable
private fun ChargeDialogPreview() {
    val sampleChargeOptions = listOf("Bank Fee", "Overdue Fee", "Processing Fee")

    AppTheme {
        AddChargeBottomSheet(
            title = "Add Charge",
            confirmText = "Confirm",
            dismissText = "Cancel",
            showDatePicker = false,
            selectedChargeName = sampleChargeOptions[0],
            selectedDate = "2025-09-06",
            chargeAmount = "1500",
            chargeType = "Flat",
            chargeCollectedOn = "2025-09-06",
            chargeOptions = sampleChargeOptions,
            onConfirm = { },
            onDismiss = { },
            onChargeSelected = { _, _ -> },
            onDatePick = { },
            onDateChange = { },
            onAmountChange = {},
        )
    }
}

@Preview
@Composable
private fun AppActionsChargeListingComponentPreview() {
    AppTheme {
        AppActionsChargeListingComponent(
            chargeTitle = "Bank Fee",
            type = "Flat",
            date = "06-09-2025",
            collectedOn = "06-09-2025",
            amount = "1500 UGX",
            onActionClicked = { },
            isExpanded = true,
            onExpandToggle = {},
        )
    }
}

@Preview
@Composable
private fun PreviewAppActionsShareListingComponent() {
    val sampleMenu = listOf(
        Actions.Edit(),
        Actions.Delete(),
    )

    AppTheme {
        AppActionsShareListingComponent(
            accountNo = "AC123456",
            shareProductName = "Gold Savings Plan",
            pendingForApprovalShares = 5,
            approvedShares = 10,
            isExpanded = true,
            onClick = {},
            menuList = sampleMenu,
            onActionClicked = { },
        )
    }
}
