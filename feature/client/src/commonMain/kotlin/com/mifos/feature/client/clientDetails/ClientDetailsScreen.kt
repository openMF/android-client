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

package com.mifos.feature.client.clientDetails

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client
import androidclient.feature.client.generated.resources.feature_client_account_number
import androidclient.feature.client.generated.resources.feature_client_accounts
import androidclient.feature.client.generated.resources.feature_client_activate_client
import androidclient.feature.client.generated.resources.feature_client_activation_date
import androidclient.feature.client.generated.resources.feature_client_add_loan_account
import androidclient.feature.client.generated.resources.feature_client_add_savings_account
import androidclient.feature.client.generated.resources.feature_client_charges
import androidclient.feature.client.generated.resources.feature_client_client_image_deleted
import androidclient.feature.client.generated.resources.feature_client_client_image_updated
import androidclient.feature.client.generated.resources.feature_client_client_not_found
import androidclient.feature.client.generated.resources.feature_client_delete_image
import androidclient.feature.client.generated.resources.feature_client_documents
import androidclient.feature.client.generated.resources.feature_client_external_id
import androidclient.feature.client.generated.resources.feature_client_group
import androidclient.feature.client.generated.resources.feature_client_identifiers
import androidclient.feature.client.generated.resources.feature_client_loan_account
import androidclient.feature.client.generated.resources.feature_client_more_client_info
import androidclient.feature.client.generated.resources.feature_client_notes
import androidclient.feature.client.generated.resources.feature_client_office
import androidclient.feature.client.generated.resources.feature_client_phone_no
import androidclient.feature.client.generated.resources.feature_client_pinpoint_location
import androidclient.feature.client.generated.resources.feature_client_please_select
import androidclient.feature.client.generated.resources.feature_client_savings_account
import androidclient.feature.client.generated.resources.feature_client_survey
import androidclient.feature.client.generated.resources.feature_client_take_new_image
import androidclient.feature.client.generated.resources.feature_client_upload_new_image
import androidclient.feature.client.generated.resources.feature_client_upload_signature
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Utils
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosUserImage
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.client.utils.rememberPlatformCameraLauncher
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme

/**
 * Created by Aditya Gupta on 18/03/24.
 */

@Composable
internal fun ClientDetailsScreen(
    onBackPressed: () -> Unit,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    charges: (Int) -> Unit,
    documents: (Int) -> Unit,
    identifiers: (Int) -> Unit,
    moreClientInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    pinpointLocation: (Int) -> Unit,
    survey: (Int) -> Unit,
    uploadSignature: (Int, String, String) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
    activateClient: (Int) -> Unit,
    clientDetailsViewModel: ClientDetailsViewModel = koinViewModel(),
) {
    val clientId by clientDetailsViewModel.clientId.collectAsStateWithLifecycle()

    val state = clientDetailsViewModel.clientDetailsUiState.collectAsStateWithLifecycle().value
    val client = clientDetailsViewModel.client.collectAsStateWithLifecycle().value
    val showLoading = clientDetailsViewModel.showLoading.collectAsStateWithLifecycle().value

    var clientNotFoundError by rememberSaveable { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSelectImageDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    val galleryLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file ->
        file?.let {
            showSelectImageDialog = false
            clientDetailsViewModel.saveClientImage(clientId, it)
        }
    }

    val cameraLauncher = rememberPlatformCameraLauncher(
        onImageCapturedPath = { file ->
            showSelectImageDialog = false
            clientDetailsViewModel.saveClientImage(clientId, file)
        },
    )

    LaunchedEffect(key1 = true) {
        clientDetailsViewModel.loadClientDetailsAndClientAccounts(clientId)
    }

    when (state) {
        is ClientDetailsUiState.ShowClientImageDeletedSuccessfully -> {
            LaunchedEffect(key1 = state) {
                snackbarHostState.showSnackbar(message = getString(Res.string.feature_client_client_image_deleted))
            }
        }

        is ClientDetailsUiState.ShowUploadImageSuccessfully -> {
            LaunchedEffect(key1 = state.response) {
                snackbarHostState.showSnackbar(message = getString(Res.string.feature_client_client_image_updated))
            }
        }

        is ClientDetailsUiState.ShowError -> {
            when (state.message == "null") {
                true -> {
                    clientNotFoundError = true
                }

                false -> {
                    LaunchedEffect(key1 = state.message) {
                        snackbarHostState.showSnackbar(message = state.message)
                    }
                }
            }
        }

        ClientDetailsUiState.Empty -> {}
    }

    MifosScaffold(
        title = stringResource(Res.string.feature_client),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = MifosIcons.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                modifier = Modifier.background(KptTheme.colorScheme.surface),
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_add_loan_account),
                    onClick = {
                        addLoanAccount(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_add_savings_account),
                    onClick = {
                        addSavingsAccount(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_charges),
                    onClick = {
                        charges(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_documents),
                    onClick = {
                        documents(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_identifiers),
                    onClick = {
                        identifiers(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_more_client_info),
                    onClick = {
                        moreClientInfo(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_notes),
                    onClick = {
                        notes(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_pinpoint_location),
                    onClick = {
                        pinpointLocation(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_survey),
                    onClick = {
                        survey(clientId)
                        showMenu = false
                    },
                )
                MifosMenuDropDownItem(
                    option = stringResource(Res.string.feature_client_upload_signature),
                    onClick = {
                        client?.displayName?.let { name ->
                            client.accountNo?.let { accountNo ->
                                uploadSignature(clientId, name, accountNo)
                            }
                        }
                        showMenu = false
                    },
                )
            }
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            if (client?.active == false) {
                Button(
                    onClick = { activateClient(clientId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(DesignToken.spacing.dp44)
                        .padding(horizontal = KptTheme.spacing.md),
                    contentPadding = PaddingValues(),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_activate_client),
                        style = KptTheme.typography.bodyLarge,
                    )
                }
            }
        },
    ) { padding ->
        if (showSelectImageDialog) {
            MifosSelectImageDialog(
                onDismissRequest = {
                    run {
                        showSelectImageDialog = !showSelectImageDialog
                    }
                },
                takeImage = {
                    cameraLauncher.launch()
                },
                uploadImage = {
                    galleryLauncher.launch()
                },
                deleteImage = {
                    clientDetailsViewModel.deleteClientImage(clientId)
                    showSelectImageDialog = false
                },
            )
        }
        if (clientNotFoundError) {
            MifosSweetError(message = stringResource(Res.string.feature_client_client_not_found)) {
                clientDetailsViewModel.loadClientDetailsAndClientAccounts(clientId)
            }
        } else {
            if (showLoading) {
                MifosProgressIndicator()
            } else {
                MifosClientDetailsScreen(
                    padding = padding,
                    loanAccountSelected = loanAccountSelected,
                    savingsAccountSelected = savingsAccountSelected,
                    onClick = {
                        showSelectImageDialog = true
                    },
                )
            }
        }
    }
}

@Composable
private fun MifosClientDetailsScreen(
    loanAccountSelected: (Int) -> Unit,
    padding: PaddingValues,
    onClick: () -> Unit,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
    clientDetailsViewModel: ClientDetailsViewModel = koinViewModel(),
) {
    val client = clientDetailsViewModel.client.collectAsStateWithLifecycle().value
    val loanAccounts = clientDetailsViewModel.loanAccount.collectAsStateWithLifecycle().value
    val savingsAccounts = clientDetailsViewModel.savingsAccounts.collectAsStateWithLifecycle().value
    val profileImage = clientDetailsViewModel.profileImage.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            MifosUserImage(
                bitmap = profileImage.value,
                modifier = Modifier
                    .size(DesignToken.sizes.dp100)
                    .clickable(onClick = onClick),
            )
        }

        Spacer(modifier = Modifier.height(DesignToken.spacing.dp10))
        client?.displayName?.let {
            Text(
                modifier = Modifier.padding(KptTheme.spacing.md),
                text = it,
                style = KptTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
            )
        }
        Spacer(modifier = Modifier.height(DesignToken.spacing.mediumSmall))
        client?.accountNo?.let {
            MifosClientDetailsText(
                icon = MifosIcons.Numbers,
                field = stringResource(Res.string.feature_client_account_number),
                value = it,
            )
        }
        client?.externalId?.let {
            MifosClientDetailsText(
                icon = MifosIcons.Numbers,
                field = stringResource(Res.string.feature_client_external_id),
                value = it,
            )
        }
        client?.let { Utils.getStringOfDate(it.activationDate) }?.let {
            MifosClientDetailsText(
                icon = MifosIcons.DateRange,
                field = stringResource(Res.string.feature_client_activation_date),
                value = it,
            )
        }
        client?.officeName?.let {
            MifosClientDetailsText(
                icon = MifosIcons.HomeWork,
                field = stringResource(Res.string.feature_client_office),
                value = it,
            )
        }
        client?.mobileNo?.let {
            MifosClientDetailsText(
                icon = MifosIcons.MobileFriendly,
                field = stringResource(Res.string.feature_client_phone_no),
                value = it,
            )
        }
        client?.groups?.let { list ->
            list.forEach { group ->
                group.name?.let {
                    MifosClientDetailsText(
                        icon = MifosIcons.Groups,
                        field = stringResource(Res.string.feature_client_group),
                        value = it,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))
        if (loanAccounts != null && savingsAccounts != null) {
            Text(
                modifier = Modifier.padding(start = KptTheme.spacing.md, bottom = DesignToken.spacing.mediumSmall),
                text = stringResource(Res.string.feature_client_accounts),
                style = KptTheme.typography.titleMedium,
                textAlign = TextAlign.Start,
            )
        }
        loanAccounts?.let {
            MifosLoanAccountExpendableCard(
                stringResource(Res.string.feature_client_loan_account),
                it,
                loanAccountSelected,
            )
        }
        savingsAccounts?.let {
            MifosSavingsAccountExpandableCard(
                stringResource(Res.string.feature_client_savings_account),
                it,
                savingsAccountSelected,
            )
        }
    }
}

@Composable
private fun MifosLoanAccountExpendableCard(
    accountType: String,
    loanAccounts: List<LoanAccountEntity>,
    loanAccountSelected: (Int) -> Unit,
) {
    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = "",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ),
        shape = DesignToken.shapes.dp22,
        colors = CardDefaults.cardColors(KptTheme.colorScheme.secondary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.padding.dp10),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = KptTheme.spacing.sm),
                    text = accountType,
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                )
                IconButton(
                    modifier = Modifier
                        .size(DesignToken.sizes.iconMedium),
                    onClick = { expendableState = !expendableState },
                ) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = MifosIcons.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }

            if (expendableState) {
                Spacer(modifier = Modifier.height(DesignToken.spacing.dp10))
                MifosLoanAccountsLazyColumn(loanAccounts, loanAccountSelected)
            }
        }
    }
}

@Composable
private fun MifosLoanAccountsLazyColumn(
    loanAccounts: List<LoanAccountEntity>,
    loanAccountSelected: (Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm),
        shape = DesignToken.shapes.dp22,
        colors = CardDefaults.cardColors(KptTheme.colorScheme.secondary),
    ) {
        LazyColumn(
            modifier = Modifier
                .height((loanAccounts.size * 52).dp)
                .padding(DesignToken.padding.dp6),
        ) {
            items(loanAccounts) { loanAccount ->
                Row(
                    modifier = Modifier
                        .padding(DesignToken.padding.dp5)
                        .clickable(
                            onClick = {
                                loanAccount.id?.let {
                                    loanAccountSelected(
                                        it,
                                    )
                                }
                            },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(DesignToken.sizes.iconAverage)
                            .padding(KptTheme.spacing.xs),
                        onDraw = {
                            drawCircle(
                                color = when {
                                    loanAccount.status?.active == true -> {
                                        Color.Green
                                    }

                                    loanAccount.status?.waitingForDisbursal == true -> {
                                        Color.Blue
                                    }

                                    loanAccount.status?.pendingApproval == true -> {
                                        Color.Yellow
                                    }

                                    loanAccount.status?.active == true && loanAccount.inArrears == true -> {
                                        Color.Red
                                    }

                                    else -> {
                                        Color.DarkGray
                                    }
                                },
                            )
                        },
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = KptTheme.spacing.xs),
                    ) {
                        loanAccount.productName?.let {
                            Text(
                                text = it,
                                style = KptTheme.typography.bodyLarge,
                                textAlign = TextAlign.Start,
                            )
                        }
                        Text(
                            text = loanAccount.accountNo.toString(),
                            style = KptTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                        )
                    }
                    loanAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = KptTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MifosSavingsAccountExpandableCard(
    accountType: String,
    savingsAccount: List<SavingsAccountEntity>,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
) {
    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = "",
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing,
                ),
            ),
        shape = DesignToken.shapes.dp22,
        colors = CardDefaults.cardColors(KptTheme.colorScheme.secondary),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignToken.padding.dp10),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = KptTheme.spacing.sm),
                    text = accountType,
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                )
                IconButton(
                    modifier = Modifier
                        .size(DesignToken.sizes.iconMedium),
                    onClick = { expendableState = !expendableState },
                ) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = MifosIcons.KeyboardArrowDown,
                        contentDescription = null,
                    )
                }
            }

            if (expendableState) {
                Spacer(modifier = Modifier.height(DesignToken.spacing.dp10))
                MifosSavingsAccountsLazyColumn(savingsAccount, savingsAccountSelected)
            }
        }
    }
}

@Composable
private fun MifosSavingsAccountsLazyColumn(
    savingsAccounts: List<SavingsAccountEntity>,
    savingsAccountSelected: (Int, SavingAccountDepositTypeEntity) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(KptTheme.spacing.sm),
        shape = DesignToken.shapes.dp22,
        colors = CardDefaults.cardColors(KptTheme.colorScheme.surface),
    ) {
        LazyColumn(
            modifier = Modifier
                .height((savingsAccounts.size * 50).dp)
                .padding(DesignToken.padding.dp6),
        ) {
            items(savingsAccounts) { savingsAccount ->
                Row(
                    modifier = Modifier
                        .padding(DesignToken.padding.dp5)
                        .clickable(
                            onClick = {
                                savingsAccount.id?.let {
                                    savingsAccount.depositType?.let { it1 ->
                                        savingsAccountSelected(
                                            it,
                                            it1,
                                        )
                                    }
                                }
                            },
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(DesignToken.sizes.iconAverage)
                            .padding(KptTheme.spacing.xs),
                        onDraw = {
                            drawCircle(
                                color = when {
                                    savingsAccount.status?.active == true -> {
                                        Color.Green
                                    }

                                    savingsAccount.status?.approved == true -> {
                                        Color.Blue
                                    }

                                    savingsAccount.status?.submittedAndPendingApproval == true -> {
                                        Color.Yellow
                                    }

                                    else -> {
                                        Color.DarkGray
                                    }
                                },
                            )
                        },
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = KptTheme.spacing.xs),
                    ) {
                        savingsAccount.productName?.let {
                            Text(
                                text = it,
                                style = KptTheme.typography.bodyLarge,
                                textAlign = TextAlign.Start,
                            )
                        }
                        Text(
                            text = savingsAccount.accountNo.toString(),
                            style = KptTheme.typography.bodyMedium,
                            textAlign = TextAlign.Start,
                        )
                    }
                    savingsAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = KptTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MifosSelectImageDialog(
    onDismissRequest: () -> Unit,
    takeImage: () -> Unit,
    uploadImage: () -> Unit,
    deleteImage: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Card(
            colors = CardDefaults.cardColors(KptTheme.colorScheme.surface),
            shape = DesignToken.shapes.largeIncreased,
        ) {
            Column(
                modifier = Modifier
                    .padding(DesignToken.padding.dp30),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.feature_client_please_select),
                    modifier = Modifier.fillMaxWidth(),
                    style = KptTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(DesignToken.spacing.largeIncreased))

                Button(
                    onClick = { takeImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_take_new_image),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { uploadImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.primary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_upload_new_image),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    onClick = { deleteImage() },
                    colors = ButtonDefaults.buttonColors(KptTheme.colorScheme.secondary),
                ) {
                    Text(
                        text = stringResource(Res.string.feature_client_delete_image),
                        modifier = Modifier.fillMaxWidth(),
                        style = KptTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun MifosClientDetailsText(icon: ImageVector, field: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = KptTheme.spacing.sm, horizontal = KptTheme.spacing.md)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(DesignToken.sizes.dp18),
            imageVector = icon,
            contentDescription = null,
            tint = KptTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = KptTheme.spacing.md),
            text = field,
            style = KptTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
        Text(
            text = value,
            style = KptTheme.typography.bodyLarge,
            textAlign = TextAlign.Start,
        )
    }
}

@DevicePreview
@Composable
private fun ClientDetailsScreenPreview() {
    ClientDetailsScreen(
        onBackPressed = {},
        addLoanAccount = {},
        addSavingsAccount = {},
        charges = {},
        documents = {},
        identifiers = {},
        moreClientInfo = {},
        notes = {},
        pinpointLocation = {},
        survey = {},
        uploadSignature = { _, _, _ -> },
        loanAccountSelected = {},
        savingsAccountSelected = { _, _ -> },
        activateClient = {},
    )
}
