/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.client.address

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.effects.EventsEffect
import kpt.core.base.ui.screen.ScreenContent
import kpt.core.database.client.entity.ClientAddressEntity
import kpt.core.designsystem.component.AppScaffold
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.AppColors
import kpt.core.designsystem.theme.AppTypography
import kpt.core.designsystem.theme.DesignToken
import kpt.core.ui.components.AppAddressCard
import kpt.feature.client.generated.resources.Res
import kpt.feature.client.generated.resources.feature_client_address
import kpt.feature.client.generated.resources.feature_client_address_line_1
import kpt.feature.client.generated.resources.feature_client_address_line_2
import kpt.feature.client.generated.resources.feature_client_address_line_3
import kpt.feature.client.generated.resources.feature_client_city
import kpt.feature.client.generated.resources.feature_client_country
import kpt.feature.client.generated.resources.feature_client_empty_address_card_message
import kpt.feature.client.generated.resources.feature_client_empty_address_card_title
import kpt.feature.client.generated.resources.feature_client_identifiers_item
import kpt.feature.client.generated.resources.feature_client_postal_code
import kpt.feature.client.generated.resources.feature_client_province
import kpt.feature.client.generated.resources.feature_client_screen_address
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Carried over from `6b66e8a43:feature/client/clientAddress/ClientAddressScreen.kt` — the header
 * with its "N item" count, the `EmptyAddressCard`, and one `AppAddressCard` per address with the
 * original's seven labelled rows in the original's order.
 *
 * §6a's three adaptations: `AddressListScreenState` becomes [ScreenState] through [ScreenContent]
 * (see the ViewModel for what that deletes); actions unchanged; the add-address "+" and the search
 * icon are `null`/absent — the add form is a 610-line screen that is not ported, and `SearchRoute`
 * is unreachable from a feature module.
 */
@Composable
internal fun ClientAddressScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    onAddAddress: ((Int) -> Unit)? = null,
    viewModel: ClientAddressViewModel = koinViewModel(),
) {
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            ClientAddressEvent.NavigateBack -> onNavigateBack()
            is ClientAddressEvent.ShowAddressForm -> onAddAddress?.invoke(event.clientId)
        }
    }

    AppScaffold(
        title = stringResource(Res.string.feature_client_screen_address),
        // Dispatched, not called directly: the ViewModel owns the event, which is the
        // path `ClientAddressAction.NavigateBack` already declared and nothing reached.
        onBackPressed = { viewModel.trySendAction(ClientAddressAction.NavigateBack) },
    ) { scaffoldPadding ->
        ClientAddressContent(
            addresses = addresses,
            onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
            canAdd = onAddAddress != null,
            modifier = modifier,
            scaffoldPadding = scaffoldPadding,
        )
    }
}

/** Stateless half — rendered in tests and previews without Koin. */
@Composable
internal fun ClientAddressContent(
    addresses: ScreenState<List<ClientAddressEntity>>,
    onAction: (ClientAddressAction) -> Unit,
    modifier: Modifier = Modifier,
    canAdd: Boolean = true,
    /**
     * Insets from the host [AppScaffold]'s top bar.
     *
     * Defaulted so tests and previews can render this half alone — the production caller always
     * passes the scaffold's own padding. These screens had NO top bar at all before S5d: the shell
     * draws one only for tab roots, so every client push destination rendered with no title and no
     * back affordance (which is why their `NavigateBack` action had no dispatcher).
     */
    scaffoldPadding: PaddingValues = PaddingValues(),
) {
    // The host scaffold's insets, folded into the modifier this half already receives so
    // the screen's own root layout places them.
    @Suppress("NAME_SHADOWING")
    val modifier = modifier.padding(scaffoldPadding)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = KptTheme.spacing.md)
            .testTag(ClientAddressTestTags.SCREEN),
    ) {
        Header(
            // Derived from the same state the list renders, so it cannot disagree with it.
            totalItem = ((addresses as? ScreenState.Content)?.data?.size ?: 0).toString(),
            canAdd = canAdd,
            onAction = onAction,
        )
        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        ScreenContent(
            state = addresses,
            onRetry = { onAction(ClientAddressAction.OnRetry) },
            modifier = Modifier.fillMaxSize().testTag(ClientAddressTestTags.LIST),
        ) { list, _ ->
            if (list.isEmpty()) {
                EmptyAddressCard()
                return@ScreenContent
            }

            // The original keyed items by `address.indexOf(it)` — an O(n) scan per item that
            // returns the FIRST match, so two identical addresses would share a key and
            // LazyColumn would mis-recycle them. Keyed by `addressId` instead, which is what the
            // entity has for exactly this.
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(list, key = { it.addressId }) { address ->
                    AppAddressCard(
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

@Composable
private fun Header(
    totalItem: String,
    canAdd: Boolean,
    onAction: (ClientAddressAction) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = stringResource(Res.string.feature_client_address),
                style = AppTypography.titleMedium,
            )
            Text(
                text = totalItem + " " + stringResource(Res.string.feature_client_identifiers_item),
                style = AppTypography.labelMedium,
                modifier = Modifier.testTag(ClientAddressTestTags.COUNT),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Search icon omitted — unreachable from a feature module, see the file header.
        if (canAdd) {
            IconButton(
                onClick = { onAction(ClientAddressAction.ShowAddressForm) },
                modifier = Modifier.testTag(ClientAddressTestTags.ADD),
            ) {
                Icon(imageVector = AppIcons.Add, contentDescription = null)
            }
        }
    }
}

@Composable
private fun EmptyAddressCard() {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth().testTag(ClientAddressTestTags.EMPTY),
        border = BorderStroke(
            width = DesignToken.strokes.thin,
            color = AppColors.cardBorders,
        ),
    ) {
        Column(modifier = Modifier.padding(KptTheme.spacing.md)) {
            Text(
                text = stringResource(Res.string.feature_client_empty_address_card_title),
                style = AppTypography.titleSmallEmphasized,
            )
            Spacer(modifier = Modifier.height(DesignToken.padding.medium))
            Text(
                text = stringResource(Res.string.feature_client_empty_address_card_message),
                style = AppTypography.bodySmall,
            )
        }
    }
}
