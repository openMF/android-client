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
import androidclient.feature.client.generated.resources.client_profile_profile
import androidclient.feature.client.generated.resources.client_profile_title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                    ClientProfileActionItem.General -> {}
                    ClientProfileActionItem.Identifiers -> {
                        identifiers(state.client?.id ?: -1)
                    }
                    ClientProfileActionItem.Notes -> {
                        notes(state.client?.id ?: -1)
                    }
                }
            }

            ClientProfileEvent.NavigateToClientDetailsScreen -> {
                navigateToClientDetailsScreen(state.client?.id ?: -1)
            }
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
                    name = state.client?.displayName ?: "",
                    accountNo = state.client?.accountNo,
                    office = state.client?.officeName ?: "",
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
