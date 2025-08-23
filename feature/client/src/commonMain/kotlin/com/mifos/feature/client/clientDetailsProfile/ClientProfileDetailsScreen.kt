/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientDetailsProfile

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.arrow_downward
import androidclient.feature.client.generated.resources.arrow_up
import androidclient.feature.client.generated.resources.client_profile_actions
import androidclient.feature.client.generated.resources.client_profile_details_title
import androidclient.feature.client.generated.resources.pen_icon
import androidclient.feature.client.generated.resources.scroll_for_more_options
import androidclient.feature.client.generated.resources.update_details
import androidclient.feature.client.generated.resources.update_photo
import androidclient.feature.client.generated.resources.update_signature
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosTextButton
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosDefaultListingComponentFromStringResources
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.EventsEffect
import com.mifos.core.ui.util.TextUtil
import com.mifos.feature.client.clientDetailsProfile.components.ClientDetailsProfile
import com.mifos.feature.client.clientDetailsProfile.components.ClientProfileDetailsActionItem
import com.mifos.feature.client.clientDetailsProfile.components.clientsDetailsActionItems
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientProfileDetailsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ClientProfileDetailsViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientProfileDetailsEvent.NavigateBack -> {
                onNavigateBack.invoke()
            }

            is ClientProfileDetailsEvent.OnActionClick -> {
                when (event.action) {
                    ClientProfileDetailsActionItem.AddCharge -> {}
                    ClientProfileDetailsActionItem.ApplyNewApplication -> {}
                    ClientProfileDetailsActionItem.AssignStaff -> {}
                    ClientProfileDetailsActionItem.ClientScreenReports -> {}
                    ClientProfileDetailsActionItem.ClosureApplication -> {}
                    ClientProfileDetailsActionItem.CreateCollateral -> {}
                    ClientProfileDetailsActionItem.CreateSelfServiceUsers -> {}
                    ClientProfileDetailsActionItem.CreateStandingInstructions -> {}
                    ClientProfileDetailsActionItem.TransferClient -> {}
                    ClientProfileDetailsActionItem.UpdateDefaultAccount -> {}
                    ClientProfileDetailsActionItem.ViewStandingInstructions -> {}
                }
            }
        }
    }

    ClientProfileDetailsScaffold(
        modifier = modifier,
        state = state,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )

    ClientProfileDetailsDialogs(
        state = state,
        onRetry = remember(viewModel) {
            {
                viewModel.trySendAction(ClientProfileDetailsAction.OnRetry)
            }
        },
    )
}

@Composable
private fun ClientProfileDetailsScaffold(
    state: ClientProfileDetailsState,
    modifier: Modifier = Modifier,
    onAction: (ClientProfileDetailsAction) -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.client_profile_details_title),
        onBackPressed = { onAction(ClientProfileDetailsAction.NavigateBack) },
        modifier = modifier,
    ) { paddingValues ->
        if (state.dialogState == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        vertical = DesignToken.padding.small,
                        horizontal = DesignToken.padding.large,
                    ),
            ) {
                ClientDetailsProfile(
                    image = state.profileImage,
                    name = state.client?.displayName,
                    mobile = state.client?.mobileNo,
                    email = state.client?.emailAddress,
                )
                Spacer(Modifier.height(DesignToken.padding.medium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    MifosTextButton(
                        onClick = {
                            onAction(ClientProfileDetailsAction.OnUpdatePhotoClick)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.arrow_up),
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(Res.string.update_photo),
                                style = MifosTypography.labelMediumEmphasized,
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(DesignToken.padding.small))
                    MifosTextButton(
                        onClick = {
                            onAction(ClientProfileDetailsAction.OnUpdateSignatureClick)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.update_signature),
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(Res.string.update_signature),
                                style = MifosTypography.labelMediumEmphasized,
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(DesignToken.padding.large))

                state.details.forEach { (sectionName, dataMap) ->
                    if (dataMap.isNotEmpty()) {
                        MifosDefaultListingComponentFromStringResources(data = dataMap)
                        Spacer(Modifier.height(DesignToken.padding.large))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(Res.drawable.arrow_downward),
                            contentDescription = null,
                            modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        )
                        Spacer(Modifier.width(DesignToken.padding.small))
                        Text(
                            text = stringResource(Res.string.scroll_for_more_options),
                            style = MifosTypography.tag,
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                        )
                    }
                    Spacer(Modifier.width(DesignToken.padding.small))
                    MifosTextButton(
                        onClick = {
                            onAction(ClientProfileDetailsAction.OnUpdateDetailsClick)
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(Res.drawable.pen_icon),
                                contentDescription = null,
                                modifier = Modifier.size(DesignToken.sizes.iconAverage),
                            )
                        },
                        text = {
                            Text(
                                text = stringResource(Res.string.update_details),
                                style = MifosTypography.labelMediumEmphasized,
                            )
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.height(DesignToken.padding.large))
                Text(
                    text = stringResource(Res.string.client_profile_actions),
                    style = MifosTypography.labelLargeEmphasized,
                )

                Spacer(Modifier.height(DesignToken.padding.large))
                clientsDetailsActionItems.forEach {
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
                                onAction(ClientProfileDetailsAction.OnActionClick(it))
                            }
                            .padding(vertical = DesignToken.padding.medium),
                    )
                }
            }
        }
    }
}

@Composable
private fun ClientProfileDetailsDialogs(
    state: ClientProfileDetailsState,
    onRetry: () -> Unit,
) {
    when (state.dialogState) {
        is ClientProfileDetailsState.DialogState.Loading -> MifosProgressIndicator()

        is ClientProfileDetailsState.DialogState.Error -> {
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
