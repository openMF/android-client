/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientCollateralDetails

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_profile_general_action_title_collateral_data
import androidclient.feature.client.generated.resources.client_savings_item
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.network.model.CollateralItemResult
import com.mifos.core.ui.components.MifosActionsCollateralDataListingComponent
import com.mifos.core.ui.components.MifosBreadcrumbNavBar
import com.mifos.core.ui.components.MifosEmptyCard
import com.mifos.core.ui.components.MifosErrorComponent
import com.mifos.core.ui.components.MifosProgressIndicator
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ClientCollateralDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ClientCollateralDetailViewmodel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    ClientCollateralDetailScreenContent(
        state = state,
        navController = navController,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        modifier = modifier,
    )
}

@Composable
internal fun ClientCollateralDetailScreenContent(
    state: ClientCollateralDetailsState,
    navController: NavController,
    modifier: Modifier = Modifier,
    onAction: (ClientCollateralDetailsAction) -> Unit,
) {
    MifosScaffold(
        title = "",
        onBackPressed = { },
        modifier = modifier,
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            MifosBreadcrumbNavBar(navController)

            when (state.state) {
                ClientCollateralDetailsState.State.Empty -> {
                    MifosEmptyCard(modifier = Modifier.padding(horizontal = DesignToken.padding.large))
                }

                is ClientCollateralDetailsState.State.Error -> {
                    MifosErrorComponent(
                        isNetworkConnected = state.state.isNetworkAvailable,
                        isRetryEnabled = true,
                        onRetry = {
                            onAction(ClientCollateralDetailsAction.OnRetry)
                        },
                        message = state.state.message,
                    )
                }

                ClientCollateralDetailsState.State.Loading -> {
                    MifosProgressIndicator()
                }

                ClientCollateralDetailsState.State.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DesignToken.padding.large),
                    ) {
                        CollateralDetailsScreenHeader(state.collaterals.size.toString())

                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                                .padding(top = DesignToken.padding.large),
                            verticalArrangement = Arrangement.spacedBy(DesignToken.padding.small),
                        ) {
                            items(
                                items = state.collaterals,
                                key = { it.collateralId },
                            ) { item ->
                                CollateralItemCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollateralDetailsScreenHeader(
    totalItem: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Text(
                text = stringResource(Res.string.client_profile_general_action_title_collateral_data),
                style = MifosTypography.titleMedium,
            )

            Text(
                text = totalItem + " " + stringResource(Res.string.client_savings_item),
                style = MifosTypography.labelMedium,
            )
        }
    }
}

@Composable
private fun CollateralItemCard(item: CollateralItemResult) {
    val total = item.basePrice * item.quantity
    val totalCollateral = (total * item.pctToBase) / 100
    MifosActionsCollateralDataListingComponent(
        name = item.name,
        quantity = item.quantity.toString(),
        totalValue = total.toString(),
        totalCollateralValue = totalCollateral.toString(),
        menuList = emptyList(),
        onActionClicked = {},
    )
}
