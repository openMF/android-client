/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientAddress

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_savings_item
import androidclient.feature.client.generated.resources.feature_client_address
import androidclient.feature.client.generated.resources.feature_client_address_line_1
import androidclient.feature.client.generated.resources.feature_client_address_line_2
import androidclient.feature.client.generated.resources.feature_client_address_line_3
import androidclient.feature.client.generated.resources.feature_client_city
import androidclient.feature.client.generated.resources.feature_client_country
import androidclient.feature.client.generated.resources.feature_client_empty_address_card_message
import androidclient.feature.client.generated.resources.feature_client_empty_address_card_title
import androidclient.feature.client.generated.resources.feature_client_postal_code
import androidclient.feature.client.generated.resources.feature_client_province
import androidclient.feature.client.generated.resources.search
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosAddressCard
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import com.mifos.core.ui.util.EventsEffect
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientAddressScreen(
    onNavigateBack: () -> Unit,
    navigateToAddAddressForm: (Int) -> Unit,
    navController: NavController,
    viewModel: ClientAddressViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadClientAddress()
    }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientAddressEvent.NavigateBack -> onNavigateBack.invoke()
            ClientAddressEvent.ShowAddressForm -> navigateToAddAddressForm(state.id)
            else -> Unit
        }
    }

    ClientAddressDialogs(
        state = state,
        onAction = { viewModel.trySendAction(it) },
    )

    ClientAddressScaffold(
        state = state,
        navController = navController,
        onAction = { viewModel.trySendAction(it) },
    )
}

@Composable
fun ClientAddressDialogs(
    state: ClientAddressState,
    onAction: (ClientAddressAction) -> Unit,
) {
    when (state.dialogState) {
        is ClientAddressState.DialogState.Error -> {
            MifosSweetError(
                message = state.dialogState.message,
                onclick = { onAction(ClientAddressAction.OnRetry) },
            )
        }

        else -> Unit
    }
}

@Composable
private fun ClientAddressScaffold(
    state: ClientAddressState,
    navController: NavController,
    onAction: (ClientAddressAction) -> Unit,
) {
    MifosScaffold(
        title = "Client Address",
        onBackPressed = { onAction(ClientAddressAction.NavigateBack) },
    ) { paddingValues ->
        when (state.addressListScreenState) {
            is ClientAddressState.AddressListScreenState.Loading -> MifosProgressIndicator()
            is ClientAddressState.AddressListScreenState.ShowAddressList -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding(),
                        ),
                ) {
                    MifosBreadcrumbNavBar(navController)
                    Column(
                        modifier = Modifier.padding(
                            start = DesignToken.padding.large,
                            end = DesignToken.padding.large,
                        ),
                    ) {
                        ClientAddressHeader(
                            totalItem = state.address.size.toString(),
                            onAction = onAction,
                        )
                        Spacer(modifier = Modifier.height(DesignToken.padding.large))
                        if (state.address.isEmpty()) {
                            EmptyAddressCard()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize(),
                            ) {
                                items(state.address, key = ({ state.address.indexOf(it) })) { address ->
                                    MifosAddressCard(
                                        title = address.addressType,
                                        addressList = mapOf(
                                            stringResource(Res.string.feature_client_address_line_1) to address.addressLine1,
                                            stringResource(Res.string.feature_client_address_line_2) to address.addressLine2,
                                            stringResource(Res.string.feature_client_address_line_3) to address.addressLine3,
                                            stringResource(Res.string.feature_client_city) to address.city,
                                            stringResource(Res.string.feature_client_province) to address.stateName,
                                            stringResource(Res.string.feature_client_country) to address.countryName,
                                            stringResource(Res.string.feature_client_postal_code) to address.postalCode,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            ClientAddressState.AddressListScreenState.NetworkError -> {
                MifosErrorComponent(
                    isNetworkConnected = state.networkConnection,
                    isRetryEnabled = true,
                    onRetry = {
                        onAction(ClientAddressAction.OnRetry)
                    },
                )
            }
        }
    }
}

@Composable
fun ClientAddressHeader(
    totalItem: String,
    onAction: (ClientAddressAction) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.feature_client_address),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = {
                // ToDo: Implement Search Address Functionality
            },
        ) {
            Icon(
                painter = painterResource(Res.drawable.search),
                contentDescription = "",
            )
        }

        IconButton(
            onClick = { onAction(ClientAddressAction.ShowAddressForm) },
        ) {
            Icon(
                imageVector = MifosIcons.Add,
                contentDescription = "",
            )
        }
    }
}

@Composable
private fun EmptyAddressCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = AppColors.cardBorders,
        ),
    ) {
        Column(
            modifier = Modifier.padding(DesignToken.padding.large),
        ) {
            Text(
                text = stringResource(Res.string.feature_client_empty_address_card_title),
                style = MifosTypography.titleSmallEmphasized,
            )

            Spacer(modifier = Modifier.height(DesignToken.padding.medium))

            Text(
                text = stringResource(Res.string.feature_client_empty_address_card_message),
                style = MifosTypography.bodySmall,
            )
        }
    }
}
