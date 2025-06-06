/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking

import androidclient.feature.path_tracking.generated.resources.Res
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_failed_to_load_path_tracking
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_no_path_tracking_found
import androidclient.feature.path_tracking.generated.resources.feature_path_tracking_track_my_path
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.objects.users.UserLatLng
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.pathTracking.PathTrackingUiState
import com.mifos.feature.pathTracking.PathTrackingViewModel
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

@Composable
expect fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    viewModel: PathTrackingViewModel = koinViewModel(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PathTrackingScreen(
    state: PathTrackingUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onPathTrackingClick: (List<UserLatLng>) -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    userStatus: Boolean,
    modifier: Modifier = Modifier,
    updateUserStatus: (Boolean) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullToRefreshState()

    var checkPermission by remember { mutableStateOf(false) }

    if (checkPermission) {
        HandleLocationPermissionRequest(
            show = checkPermission,
            onPermissionResult = { granted ->
                if (granted) updateUserStatus(true)
                checkPermission = false
            },
        )
    }

    MifosScaffold(
        modifier = modifier,
        title = stringResource(Res.string.feature_path_tracking_track_my_path),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    if (userStatus) {
                        updateUserStatus(false)
                    } else {
                        checkPermission = true
                    }
                },
            ) {
                Icon(
                    imageVector = if (userStatus) MifosIcons.Stop else MifosIcons.MyLocation,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            PullToRefreshBox(
                state = pullRefreshState,
                onRefresh = onRefresh,
                isRefreshing = refreshState,
            ) {
                when (state) {
                    is PathTrackingUiState.Error -> {
                        MifosSweetError(message = stringResource(state.message)) {
                            onRetry()
                        }
                    }

                    is PathTrackingUiState.Loading -> MifosCircularProgress()

                    is PathTrackingUiState.PathTracking -> {
                        PathTrackingContent(
                            pathTrackingList = state.userLocations,
                            onPathTrackingClick = onPathTrackingClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PathTrackingContent(
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

    OutlinedCard(
        modifier = modifier.padding(8.dp),
        onClick = { onPathTrackingClick(latLngList) },
        colors = CardDefaults.outlinedCardColors(MaterialTheme.colorScheme.surface),
    ) {
        PathTrackingMapView(latLngList = latLngList)

        Text(
            modifier = Modifier.padding(8.dp),
            text = "${pathTracking.date} from ${pathTracking.startTime} to ${pathTracking.stopTime}",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
expect fun HandleLocationPermissionRequest(
    show: Boolean,
    onPermissionResult: (granted: Boolean) -> Unit,
)

@Composable
expect fun PathTrackingMapView(latLngList: List<UserLatLng>)

private fun getLatLngList(latLngString: String?): List<UserLatLng> {
    val json = Json { ignoreUnknownKeys = true }
    if (latLngString.isNullOrEmpty()) return emptyList()
    return json.decodeFromString(latLngString)
}

private class PathTrackingUiStateProvider : PreviewParameterProvider<PathTrackingUiState> {

    override val values: Sequence<PathTrackingUiState>
        get() = sequenceOf(
            PathTrackingUiState.Loading,
            PathTrackingUiState.Error(Res.string.feature_path_tracking_no_path_tracking_found),
            PathTrackingUiState.Error(Res.string.feature_path_tracking_failed_to_load_path_tracking),
            PathTrackingUiState.PathTracking(samplePathTrackingList),
        )
}

@DevicePreview
@Composable
private fun PathTrackingScreenPreview(
    @PreviewParameter(PathTrackingUiStateProvider::class) state: PathTrackingUiState,
) {
    PathTrackingScreen(
        state = state,
        onBackPressed = {},
        onRetry = {},
        onPathTrackingClick = {},
        onRefresh = {},
        refreshState = false,
        userStatus = false,
        updateUserStatus = {},
    )
}

val samplePathTrackingList = List(10) {
    UserLocation(
        userId = it,
        latLng = "123,456",
        date = "date $it",
        startTime = "start time $it",
        stopTime = "stop time $it",
    )
}
