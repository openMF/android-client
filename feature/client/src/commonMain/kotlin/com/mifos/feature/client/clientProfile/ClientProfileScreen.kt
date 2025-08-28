/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientProfile

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_actions
import androidclient.feature.client.generated.resources.client_profile_loan_account
import androidclient.feature.client.generated.resources.client_profile_profile
import androidclient.feature.client.generated.resources.client_profile_savings_account
import androidclient.feature.client.generated.resources.client_profile_select_account_type
import androidclient.feature.client.generated.resources.client_profile_title
import androidclient.feature.client.generated.resources.client_savings_savings_accounts
import androidclient.feature.client.generated.resources.dismiss_text
import androidclient.feature.client.generated.resources.feature_client_loan_account
import androidclient.feature.client.generated.resources.name_na
import androidclient.feature.client.generated.resources.office_na
import androidclient.feature.client.generated.resources.string_not_available
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.util.Logger
import com.mifos.core.designsystem.component.MifosButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import com.mifos.feature.client.clientProfile.components.ClientProfileActionItem
import com.mifos.feature.client.clientProfile.components.ProfileCard
import com.mifos.feature.client.clientProfile.components.clientsActionItems
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientProfileScreen(
    notes: (Int) -> Unit,
    documents: (Int) -> Unit,
    viewAssociatedLoanAccounts: (Int) -> Unit,
    viewAssociatedSavingsAccounts: (Int) -> Unit,
    identifiers: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    navigateToClientDetailsScreen: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientProfileViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientProfileEvent.NavigateBack -> {
                onNavigateBack.invoke()
            }

            is ClientProfileEvent.OnActionClick -> {
                when (event.action) {
                    ClientProfileActionItem.Address -> {}

                    ClientProfileActionItem.Documents -> {
                        documents(state.client?.id ?: -1)
                    }

                    ClientProfileActionItem.FamilyMembers -> {}

                    ClientProfileActionItem.Identifiers -> {
                        identifiers(state.client?.id ?: -1)
                    }

                    ClientProfileActionItem.Notes -> {
                        notes(state.client?.id ?: -1)
                    }

                    else -> null
                }
            }

            ClientProfileEvent.NavigateToClientDetailsScreen -> {
                navigateToClientDetailsScreen(state.client?.id ?: -1)
            }

            is ClientProfileEvent.OpenClientLoanAccounts -> viewAssociatedLoanAccounts(
                state.client?.id ?: -1,
            )

            is ClientProfileEvent.OpenClientSavingsAccounts -> viewAssociatedSavingsAccounts(
                state.client?.id ?: -1,
            )
        }
    }

    ClientProfileScaffold(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientProfileDialogs(
        state = state,
        onRetry = remember(viewModel) {
            {
                viewModel.trySendAction(ClientProfileAction.OnRetry)
            }
        },
    )
}

@Composable
private fun ClientProfileScaffold(
    state: ClientProfileState,
    modifier: Modifier = Modifier,
    onAction: (ClientProfileAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_profile_title),
        onBackPressed = { onAction(ClientProfileAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        vertical = DesignToken.padding.extraLarge,
                        horizontal = DesignToken.padding.large,
                    ),
            ) {
                Text(
                    text = stringResource(Res.string.client_profile_profile),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.medium))
                ProfileCard(
                    image = state.profileImage,
                    name = state.client?.displayName ?: stringResource(Res.string.name_na),
                    accountNo = state.client?.accountNo
                        ?: stringResource(Res.string.string_not_available),
                    office = state.client?.officeName ?: stringResource(Res.string.office_na),
                    onClick = {
                        onAction(
                            ClientProfileAction.NavigateToClientDetailsScreen,
                        )
                    },
                )
                Spacer(Modifier.height(DesignToken.padding.large))
                Text(
                    text = stringResource(Res.string.client_profile_actions),
                    style = MifosTypography.labelLargeEmphasized,
                )
                Spacer(Modifier.height(DesignToken.padding.medium))
                clientsActionItems.forEach {
                    MifosRowCard(
                        title = stringResource(it.title),
                        imageVector = it.icon,
                        leftValues = listOf(
                            TextUtil(
                                text = stringResource(it.subTitle),
                                style = MifosTypography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        rightValues = emptyList(),
                        modifier = Modifier
                            .clickable {
                                onAction(ClientProfileAction.OnActionClick(it))
                            }
                            .padding(vertical = DesignToken.padding.medium),
                    )
                }
            }
        }
    }

    if (state.showAccountChooserDialog) {
        AlertDialog(
            onDismissRequest = { onAction.invoke(ClientProfileAction.ToggleAccountChooserDialog) },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { onAction.invoke(ClientProfileAction.ToggleAccountChooserDialog) }) {
                    Text(stringResource(Res.string.dismiss_text))
                }
            },
            title = { Text(stringResource(Res.string.client_profile_select_account_type)) },
            text = {
                Column {
                    MifosButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction.invoke(ClientProfileAction.NavigateToClientSavingsAccounts) },
                        text = { Text(stringResource(Res.string.client_profile_savings_account)) },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MifosButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onAction.invoke(ClientProfileAction.NavigateToClientLoanAccounts) },
                        text = { Text(stringResource(Res.string.client_profile_loan_account)) },
                    )
                }
            },
        )
    }
}

@Composable
private fun ClientProfileDialogs(
    state: ClientProfileState,
    onRetry: () -> Unit,
) {
    when (state.dialogState) {
        is ClientProfileState.DialogState.Loading -> MifosProgressIndicator()

        is ClientProfileState.DialogState.Error -> {
            MifosErrorComponent(
                isNetworkConnected = state.networkConnection,
                message = state.dialogState.message,
                isRetryEnabled = true,
                onRetry = {
                    onRetry()
                },
            )
        }

        null -> Unit
    }
}
