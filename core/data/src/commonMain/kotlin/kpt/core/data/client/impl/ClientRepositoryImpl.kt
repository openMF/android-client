/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.data.client.impl

import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineScope
import kpt.core.base.data.annotation.FromStore
import kpt.core.base.data.annotation.RepositoryBinding
import kpt.core.base.store.screen.FetchPolicy
import kpt.core.base.store.screen.ScreenDataStream
import kpt.core.base.store.screen.asScreenStream
import kpt.core.data.client.ClientRepository
import kpt.core.database.client.entity.AddressTemplate
import kpt.core.database.client.entity.ClientAccounts
import kpt.core.database.client.entity.ClientAddressEntity
import kpt.core.database.client.entity.ClientEntity
import kpt.core.model.client.ClientCloseTemplateResponse
import kpt.core.model.client.ClientTemplateOptions
import kpt.core.model.objects.clients.ClientAddressResponse
import kpt.core.model.objects.noncoreobjects.Identifier
import kpt.core.model.shared.CollateralItem
import kpt.core.model.shared.CollateralItemResult
import kpt.core.network.mifos.client.api.ClientApi
import kpt.core.store.config.AppCacheKeys
import kpt.core.store.config.AppStoreIds
import kpt.core.store.config.AppStoreRegistry
import org.mobilenativefoundation.store.store5.Store

@RepositoryBinding(binds = ClientRepository::class)
internal class ClientRepositoryImpl(
    private val clientApi: ClientApi,
    @FromStore(AppStoreIds.Clients) private val store: Store<Unit, List<ClientEntity>>,
    @FromStore(AppStoreIds.ClientGetClient) private val clientStore: Store<Int, ClientEntity>,
    @FromStore(AppStoreIds.ClientGetClientAccounts) private val accountsStore: Store<Int, ClientAccounts>,
    @FromStore(AppStoreIds.ClientGetClientListIdentifiers)
    private val identifiersStore: Store<Long, List<Identifier>>,
    @FromStore(AppStoreIds.ClientGetClientPinpointLocations)
    private val pinpointStore: Store<Int, List<ClientAddressResponse>>,
    @FromStore(AppStoreIds.ClientGetClientCollateralItems)
    private val clientCollateralStore: Store<Int, List<CollateralItemResult>>,
    @FromStore(AppStoreIds.CollateralItems)
    private val collateralCatalogStore: Store<Unit, List<CollateralItem>>,
    @FromStore(AppStoreIds.ClientCloseTemplate)
    private val closeTemplateStore: Store<Unit, ClientCloseTemplateResponse>,
    @FromStore(AppStoreIds.ClientGetClientTemplate)
    private val clientTemplateStore: Store<Int, ClientTemplateOptions>,
    @FromStore(AppStoreIds.ClientGetClientAddresses)
    private val addressesStore: Store<Int, List<ClientAddressEntity>>,
    @FromStore(AppStoreIds.AddressTemplate)
    private val addressTemplateStore: Store<Unit, AddressTemplate>,
) : ClientRepository {

    // NETWORK_WITH_CACHE, not the CACHE_FIRST_SWR default.
    //
    // SWR's read path is `cached(key, refresh = false)` — it makes no request itself and leaves
    // revalidation to a freshness-band gate that only fires once something HAS been fetched. On a
    // cold cache (`fetchedAt == null`, the band is `Initial`) nothing ever fetches: the Clients tab
    // sat on Loading forever with zero network requests on a clean install, verified on device
    // 2026-10-01. NETWORK_WITH_CACHE asks the network on the read path and still serves Room first,
    // so the offline-first behaviour this screen needs is unchanged — a field officer with no signal
    // still gets the cached roster, and the first run actually loads.
    //
    // The ttl stays the declared freshness bound: it drives the "updated N ago" indicator, never
    // whether cache is served.
    override fun clientsStream(scope: CoroutineScope): ScreenDataStream<List<ClientEntity>> =
        store.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.Clients.LIST,
            scope = scope,
            isEmpty = { it.isEmpty() },
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.CLIENTS,
        )

    override fun clientStream(clientId: Int, scope: CoroutineScope): ScreenDataStream<ClientEntity> =
        clientStore.asScreenStream(
            key = clientId,
            cacheKey = AppCacheKeys.ClientGetClient.forKey(clientId.toString()),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT,
        )

    override fun clientAccountsStream(clientId: Int, scope: CoroutineScope): ScreenDataStream<ClientAccounts> =
        accountsStore.asScreenStream(
            key = clientId,
            cacheKey = AppCacheKeys.ClientGetClientAccounts.forKey(clientId.toString()),
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_ACCOUNTS,
        )

    override fun clientIdentifiersStream(
        clientId: Long,
        scope: CoroutineScope,
    ): ScreenDataStream<List<Identifier>> = identifiersStore.asScreenStream(
        key = clientId,
        cacheKey = AppCacheKeys.ClientGetClientListIdentifiers.forKey(clientId.toString()),
        scope = scope,
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_LIST_IDENTIFIERS,
    )

    override fun pinpointLocationsStream(
        clientId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<ClientAddressResponse>> = pinpointStore.asScreenStream(
        key = clientId,
        cacheKey = AppCacheKeys.ClientGetClientPinpointLocations.forKey(clientId.toString()),
        scope = scope,
        isEmpty = { it.isEmpty() },
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_PINPOINT_LOCATIONS,
    )

    override fun clientCollateralStream(
        clientId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<CollateralItemResult>> = clientCollateralStore.asScreenStream(
        key = clientId,
        cacheKey = AppCacheKeys.ClientGetClientCollateralItems.forKey(clientId.toString()),
        scope = scope,
        isEmpty = { it.isEmpty() },
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_COLLATERAL_ITEMS,
    )

    // The two catalogs below are NOT marked `isEmpty`. An empty collateral-type list or an empty
    // closure-reason list is a tenant MISCONFIGURATION, not an empty state the officer can act on:
    // rendering "nothing here" would invite them to keep waiting. Letting it through as Content
    // makes the form render with an empty picker, which is the honest symptom.
    override fun collateralItemsStream(scope: CoroutineScope): ScreenDataStream<List<CollateralItem>> =
        collateralCatalogStore.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.CollateralItems.LIST,
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.COLLATERAL_ITEMS,
        )

    override fun clientCloseTemplateStream(scope: CoroutineScope): ScreenDataStream<ClientCloseTemplateResponse> =
        closeTemplateStore.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.ClientCloseTemplate.LIST,
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.CLIENT_CLOSE_TEMPLATE,
        )

    override fun clientTemplateStream(
        clientId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<ClientTemplateOptions> = clientTemplateStore.asScreenStream(
        key = clientId,
        cacheKey = AppCacheKeys.ClientGetClientTemplate.forKey(clientId.toString()),
        scope = scope,
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_TEMPLATE,
    )

    /**
     * Fineract answers `GET clients/{id}/images` with a `data:image/png;base64,<payload>` STRING,
     * not binary — hence `bodyAsText()`. The `data:` prefix is left ON: `imageToByteArray` strips
     * it itself, and stripping it twice would cut into the payload of any photo whose base64
     * happens to contain a comma.
     *
     * `getOrNull()` because every failure here means one thing to the screen: no photo. A 404
     * (never uploaded) is the common case and is not an error.
     */
    override suspend fun clientImage(clientId: Int): String? = runCatching {
        clientApi.getClientImage(clientId).bodyAsText()
    }.getOrNull()

    override fun clientAddressesStream(
        clientId: Int,
        scope: CoroutineScope,
    ): ScreenDataStream<List<ClientAddressEntity>> = addressesStore.asScreenStream(
        key = clientId,
        cacheKey = AppCacheKeys.ClientGetClientAddresses.forKey(clientId.toString()),
        scope = scope,
        isEmpty = { it.isEmpty() },
        fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
        ttl = AppStoreRegistry.Ttl.CLIENT_GET_CLIENT_ADDRESSES,
    )

    override fun addressTemplateStream(scope: CoroutineScope): ScreenDataStream<AddressTemplate> =
        addressTemplateStore.asScreenStream(
            key = Unit,
            cacheKey = AppCacheKeys.AddressTemplate.LIST,
            scope = scope,
            fetchPolicy = FetchPolicy.NETWORK_WITH_CACHE,
            ttl = AppStoreRegistry.Ttl.ADDRESS_TEMPLATE,
        )
}
