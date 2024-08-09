@file:OptIn(
    ExperimentalMaterialApi::class
)

package com.mifos.feature.path_tracking

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.PermissionBox
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.user.UserLatLng
import com.mifos.core.objects.user.UserLocation
import com.mifos.feature.path.tracking.R

@Composable
fun PathTrackingScreen(
    onBackPressed: () -> Unit,
    onPathTrackingClick: (List<UserLatLng>) -> Unit
) {

    val context = LocalContext.current
    val viewModel: PathTrackingViewModel = hiltViewModel()
    val state by viewModel.pathTrackingUiState.collectAsStateWithLifecycle()
    val refreshState by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val userStatus by viewModel.userStatus.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {

        val notificationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (Constants.STOP_TRACKING == action) {
                    viewModel.loadPathTracking()
                }
            }
        }
        registerReceiver(
            context,
            notificationReceiver,
            IntentFilter(Constants.STOP_TRACKING),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(notificationReceiver)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPathTracking()
    }

    PathTrackingScreen(
        state = state,
        onBackPressed = onBackPressed,
        onRetry = {
            viewModel.loadPathTracking()
        },
        onPathTrackingClick = onPathTrackingClick,
        onRefresh = {
            viewModel.refreshCenterList()
        },
        refreshState = refreshState,
        userStatus = userStatus,
        updateUserStatus = { viewModel.updateUserStatus(it) }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PathTrackingScreen(
    state: PathTrackingUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onPathTrackingClick: (List<UserLatLng>) -> Unit,
    onRefresh: () -> Unit,
    refreshState: Boolean,
    userStatus: Boolean,
    updateUserStatus: (Boolean) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshState,
        onRefresh = onRefresh
    )
    var checkPermission by remember { mutableStateOf(false) }


    if (checkPermission) {
        PermissionBox(
            requiredPermissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            title = R.string.feature_path_tracking_permission_required,
            description = R.string.feature_path_tracking_approve_permission_description_location,
            confirmButtonText = R.string.feature_path_tracking_proceed,
            dismissButtonText = R.string.feature_path_tracking_dismiss,
            onGranted = {
                updateUserStatus(true)
            }
        )
    }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_path_tracking_track_my_path),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(
                onClick = {
                    if (userStatus) {
                        // TODO stop Path Service
                        updateUserStatus(false)
                    } else {
                        checkPermission = true
                    }
                }
            ) {
                Icon(
                    imageVector = if (userStatus) Icons.Rounded.Stop else Icons.Rounded.MyLocation,
                    contentDescription = null,
                )
            }
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                when (state) {
                    is PathTrackingUiState.Error -> {
                        MifosSweetError(message = stringResource(id = state.message)) {
                            onRetry()
                        }
                    }

                    is PathTrackingUiState.Loading -> MifosCircularProgress()

                    is PathTrackingUiState.PathTracking -> {
                        PathTrackingContent(
                            pathTrackingList = state.userLocations,
                            onPathTrackingClick = onPathTrackingClick
                        )
                    }
                }
                PullRefreshIndicator(
                    refreshing = refreshState,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}


@Composable
fun PathTrackingContent(
    pathTrackingList: List<UserLocation>,
    onPathTrackingClick: (List<UserLatLng>) -> Unit
) {
    LazyColumn {
        items(pathTrackingList) { pathTracking ->
            PathTrackingItem(
                pathTracking = pathTracking,
                onPathTrackingClick = onPathTrackingClick
            )
        }
    }
}

@Composable
fun PathTrackingItem(
    pathTracking: UserLocation,
    onPathTrackingClick: (List<UserLatLng>) -> Unit
) {
    val latLngList = getLatLngList(pathTracking.latlng)
    val latLng = latLngList[0]
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latLng.lat, latLng.lng), 15f)
    }
    val uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp),
        onClick = {
            onPathTrackingClick(latLngList)
        },
        colors = CardDefaults.outlinedCardColors(White)
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings
        )
        Text(
            modifier = Modifier.padding(8.dp),
            text = "${pathTracking.date} from ${pathTracking.start_time} to ${pathTracking.stop_time}",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal,
                color = Black
            )
        )
    }
}

private fun getLatLngList(latLngString: String?): List<UserLatLng> {
    val gson = Gson()
    return gson.fromJson(
        latLngString,
        object : TypeToken<List<UserLatLng>>() {}.type
    )
}


class PathTrackingUiStateProvider : PreviewParameterProvider<PathTrackingUiState> {

    override val values: Sequence<PathTrackingUiState>
        get() = sequenceOf(
            PathTrackingUiState.Loading,
            PathTrackingUiState.Error(R.string.feature_path_tracking_no_path_tracking_found),
            PathTrackingUiState.Error(R.string.feature_path_tracking_failed_to_load_path_tracking),
            PathTrackingUiState.PathTracking(samplePathTrackingList)
        )
}

@Preview(showBackground = true)
@Composable
private fun PathTrackingScreenPreview(
    @PreviewParameter(PathTrackingUiStateProvider::class) state: PathTrackingUiState
) {
    PathTrackingScreen(
        state = state,
        onBackPressed = {},
        onRetry = {},
        onPathTrackingClick = {},
        onRefresh = {},
        refreshState = false,
        userStatus = false,
        updateUserStatus = {}
    )
}

val samplePathTrackingList = List(10) {
    UserLocation(
        user_id = it,
        latlng = "123,456",
        date = "date $it",
        start_time = "start time $it",
        stop_time = "stop time $it",
    )
}