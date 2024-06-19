@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.path_tracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.user.UserLocation

@Composable
fun PathTrackingScreen(
    userId : Int,
    onBackPressed: () -> Unit,
    onPathTrackingClick: () -> Unit
) {

    val viewModel: PathTrackingViewModel = hiltViewModel()
    val state by viewModel.pathTrackingUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadPathTracking(userId)
    }

    PathTrackingScreen(
        state = PathTrackingUiState.PathTracking(samplePathTrackingList),
        onBackPressed = onBackPressed,
        onRetry = {},
        onPathTrackingClick = onPathTrackingClick
    )

}

@Composable
fun PathTrackingScreen(
    state: PathTrackingUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onPathTrackingClick: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var locateLocation by rememberSaveable { mutableStateOf(false) }

    MifosScaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                navigationIcon = {
                    IconButton(
                        onClick = { onBackPressed() },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Black,
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.feature_path_tracking_track_my_path),
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal
                        ),
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                },
                actions = {
                    IconButton(
                        onClick = { locateLocation = locateLocation.not() }
                    ) {
                        Icon(
                            imageVector = if (locateLocation) Icons.Rounded.Stop else Icons.Rounded.MyLocation,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
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
        }
    }
}


@Composable
fun PathTrackingContent(
    pathTrackingList: List<UserLocation>,
    onPathTrackingClick: () -> Unit
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
    onPathTrackingClick: () -> Unit
) {
    val testLatLng = LatLng(12.905150682069308, 77.5651237271759)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(testLatLng, 15f)
    }
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = false))
    }

    OutlinedCard(
        modifier = Modifier
            .padding(8.dp),
        onClick = {
            onPathTrackingClick()
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
        onPathTrackingClick = {}
    )
}

val samplePathTrackingList = List(10) {
    UserLocation(user_id = it, latlng = "123,456", date = "")
}