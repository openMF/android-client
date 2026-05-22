/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking.ui

import androidclient.feature.path_tracking.generated.resources.Res
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_approve_permission_description_location
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_dismiss
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_permission_required
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_proceed
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_track_my_path
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.component.getRequiredPermissionsForLocation
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.users.UserLatLng
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.ui.util.DevicePreview
import com.mifos.core.ui.util.EventsEffect
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import template.core.base.designsystem.theme.KptTheme
import template.core.base.ui.screen.ScreenContent

/**
 * Path-tracking screen entry point. Platform actuals (Android/Desktop/iOS/JS/WasmJs)
 * supply the `actual` body — Android dispatches the maps intent + registers a
 * `STOP_TRACKING` broadcast receiver to refresh the list when the tracking
 * service stops; the other platforms render a stub until first-class support
 * lands. The actuals delegate to [PathTrackingScreenContent] for the shared
 * Material 3 list UI.
 */
@Composable
expect fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    viewModel: PathTrackingViewModel = koinViewModel(),
)

/**
 * Per-platform map preview embedded in each list row. Android draws a Google Map
 * polyline; other platforms draw a placeholder until a cross-platform map
 * primitive is added. Genuinely platform-specific — kept as an `expect/actual`.
 */
@Composable
expect fun PathTrackingMapView(latLngList: List<UserLatLng>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PathTrackingScreenContent(
    state: PathTrackingState,
    snackbarHostState: SnackbarHostState,
    onAction: (PathTrackingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullToRefreshState()

    if (state.checkPermission) {
        PermissionBox(
            requiredPermissions = getRequiredPermissionsForLocation(),
            title = stringResource(Res.string.feature_path_tracking_permission_required),
            description = stringResource(Res.string.feature_path_tracking_approve_permission_description_location),
            confirmButtonText = stringResource(Res.string.feature_path_tracking_proceed),
            dismissButtonText = stringResource(Res.string.feature_path_tracking_dismiss),
            onGranted = { onAction(PathTrackingAction.PermissionGranted) },
        )
    }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_path_tracking_track_my_path),
        onBackPressed = { onAction(PathTrackingAction.NavigateBack) },
        actions = {
            IconButton(onClick = { onAction(PathTrackingAction.ToggleUserStatus) }) {
                Icon(
                    imageVector = if (state.userStatus) MifosIcons.Stop else MifosIcons.MyLocation,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            PullToRefreshBox(
                state = pullRefreshState,
                modifier = Modifier.fillMaxSize(),
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(PathTrackingAction.OnRefresh) },
            ) {
                ScreenContent(
                    state = state.screenState,
                    onRetry = { onAction(PathTrackingAction.OnRetry) },
                    modifier = Modifier.fillMaxSize(),
                ) { tracking, _ ->
                    PathTrackingList(
                        pathTrackingList = tracking,
                        onPathTrackingClick = { latLngs ->
                            onAction(PathTrackingAction.OnPathTrackingClick(latLngs))
                        },
                    )
                }
            }
        }
    }
}

/**
 * Shared bind helper for the platform actuals — wires the VM state + action
 * dispatch + `OpenPathInMaps` event into [PathTrackingScreenContent]. Platforms
 * supply [openInMaps] to dispatch the platform map intent / external link, and
 * an optional [sideEffects] block for platform-specific listeners (e.g. the
 * Android `STOP_TRACKING` `BroadcastReceiver` that auto-refreshes the list when
 * the background tracking service stops).
 */
@Composable
internal fun PathTrackingScreenBinding(
    viewModel: PathTrackingViewModel,
    onBackPressed: () -> Unit,
    openInMaps: (List<UserLatLng>) -> Unit,
    sideEffects: @Composable (PathTrackingViewModel) -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            PathTrackingEvent.NavigateBack -> onBackPressed()
            is PathTrackingEvent.OpenPathInMaps -> openInMaps(event.latLngs)
        }
    }

    sideEffects(viewModel)

    PathTrackingScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
    )
}

@Composable
private fun PathTrackingList(
    pathTrackingList: List<UserLocation>,
    modifier: Modifier = Modifier,
    onPathTrackingClick: (List<UserLatLng>) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        items(pathTrackingList) { pathTracking ->
            PathTrackingItem(
                pathTracking = pathTracking,
                onPathTrackingClick = onPathTrackingClick,
            )
        }
    }
}

@Composable
private fun PathTrackingItem(
    pathTracking: UserLocation,
    modifier: Modifier = Modifier,
    onPathTrackingClick: (List<UserLatLng>) -> Unit,
) {
    val latLngList = getLatLngList(pathTracking.latLng)
    var startAdd by remember { mutableStateOf<String?>("Loading...") }
    var endAdd by remember { mutableStateOf<String?>("Loading...") }
    LaunchedEffect(pathTracking.latLng) {
        if (pathTracking.startAddress == null && latLngList.isNotEmpty()) {
            startAdd = getAddressFromLatLng(
                lat = latLngList.first().lat,
                lng = latLngList.first().lng,
            )
        }
        if (pathTracking.endAddress == null && latLngList.isNotEmpty()) {
            endAdd = getAddressFromLatLng(
                lat = latLngList.last().lat,
                lng = latLngList.last().lng,
            )
        }
    }
    OutlinedCard(
        modifier = modifier.padding(KptTheme.spacing.sm),
        onClick = { onPathTrackingClick(latLngList) },
        colors = CardDefaults.outlinedCardColors(KptTheme.colorScheme.surface),
    ) {
        PathTrackingMapView(latLngList = latLngList)

        Text(
            modifier = Modifier.padding(vertical = KptTheme.spacing.xs, horizontal = KptTheme.spacing.sm),
            text = "${pathTracking.startAddress ?: startAdd} to ${pathTracking.endAddress ?: endAdd}",
            style = KptTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
        Text(
            modifier = Modifier.padding(KptTheme.spacing.sm),
            text = "${pathTracking.date} from ${pathTracking.startTime} to ${pathTracking.stopTime}",
            style = KptTheme.typography.bodySmall,
        )
    }
}

private fun getLatLngList(latLngString: String?): List<UserLatLng> {
    val json = Json { ignoreUnknownKeys = true }
    if (latLngString.isNullOrEmpty()) return emptyList()
    return json.decodeFromString(latLngString)
}

/**
 * Reverse-geocode a single (lat, lng) pair via the public OpenStreetMap
 * Nominatim service. Returns `null` on any failure (network, parse, missing
 * address). Used by both the common preview rows (when [UserLocation.startAddress]
 * is unset) and the Android map markers. No `runCatching` — explicit
 * try/catch + re-throw `CancellationException` per RULE-NO-RUN-CATCHING-001.
 */
suspend fun getAddressFromLatLng(lat: Double, lng: Double): String? {
    return try {
        val url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$lat&lon=$lng&addressdetails=1"
        val response = HttpClient().get(url)
        val jsonResponse = Json.parseToJsonElement(response.bodyAsText()).jsonObject

        val address = jsonResponse["address"]?.jsonObject
        formatAddressComponents(address)
    } catch (ce: kotlinx.coroutines.CancellationException) {
        throw ce
    } catch (t: Throwable) {
        null
    }
}

private fun formatAddressComponents(address: JsonObject?): String? {
    if (address == null) return null

    val components = listOfNotNull(
        address["house_number"]?.jsonPrimitive?.content,
        address["road"]?.jsonPrimitive?.content,
        address["neighbourhood"]?.jsonPrimitive?.content,
        address["suburb"]?.jsonPrimitive?.content,
        address["village"]?.jsonPrimitive?.content,
        address["town"]?.jsonPrimitive?.content,
        address["city"]?.jsonPrimitive?.content,
        address["municipality"]?.jsonPrimitive?.content,
        address["county"]?.jsonPrimitive?.content,
        address["state_district"]?.jsonPrimitive?.content,
        address["state"]?.jsonPrimitive?.content,
        address["postcode"]?.jsonPrimitive?.content,
        address["country"]?.jsonPrimitive?.content,
    ).filter { it.isNotBlank() }

    return components.joinToString(", ")
}

// region Previews

private val samplePathTrackingList: List<UserLocation> = List(3) {
    UserLocation(
        staffId = it,
        latLng = "[{\"lat\":12.34,\"lng\":56.78},{\"lat\":13.34,\"lng\":57.78}]",
        date = "date $it",
        startTime = "start time $it",
        stopTime = "stop time $it",
        startAddress = "Start address $it",
        endAddress = "End address $it",
    )
}

private class PathTrackingStateProvider : PreviewParameterProvider<PathTrackingState> {
    override val values: Sequence<PathTrackingState>
        get() = sequenceOf(
            PathTrackingState(screenState = ScreenState.Loading),
            PathTrackingState(screenState = ScreenState.Empty),
            PathTrackingState(
                screenState = ScreenState.Content(
                    data = samplePathTrackingList,
                    freshness = DataFreshness.FRESH,
                ),
            ),
        )
}

@DevicePreview
@Composable
private fun PathTrackingScreenContentPreview(
    @PreviewParameter(PathTrackingStateProvider::class) state: PathTrackingState,
) {
    PathTrackingScreenContent(
        state = state,
        snackbarHostState = remember { SnackbarHostState() },
        onAction = {},
    )
}

// endregion
