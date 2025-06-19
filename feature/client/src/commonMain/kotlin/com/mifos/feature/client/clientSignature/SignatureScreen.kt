/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
@file:OptIn(ExperimentalComposeUiApi::class)

package com.mifos.feature.client.clientSignature

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_add_signature
import androidclient.feature.client.generated.resources.feature_client_signature_gallery
import androidclient.feature.client.generated.resources.feature_client_signature_reset
import androidclient.feature.client.generated.resources.feature_client_signature_title
import androidclient.feature.client.generated.resources.feature_client_signature_uploaded_successfully
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider


@Composable
expect fun SignatureScreen(
    onBackPressed: () -> Unit,
)

@Composable
internal fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    onUploadFromCanvas: () -> Unit,
    onUploadFromGallery: () -> Unit,
    snackbarHostState: SnackbarHostState,
    drawColor: Color,
    drawBrush: Float,
    onResetDrawing: () -> Unit,
    modifier: Modifier = Modifier,
    drawingContent: @Composable () -> Unit,
) {
    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    val scope = rememberCoroutineScope()

    MifosScaffold(
        title = stringResource(Res.string.feature_client_signature_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = onUploadFromCanvas) {
                Icon(imageVector = MifosIcons.Upload, contentDescription = null)
            }
        },
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems()
                    .forEachIndexed { index, navigationItem ->
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(navigationItem.label)
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label,
                                )
                            },
                            onClick = {
                                navigationSelectedItem = index
                                when (index) {
                                    0 -> onResetDrawing()
                                    1 -> onUploadFromGallery()
                                }
                            },
                        )
                    }
            }
        },
        snackbarHostState = snackbarHostState,
        modifier = modifier
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (state) {
                is SignatureUiState.Error ->
                    MifosSweetError(message = stringResource(state.message)) {}

                is SignatureUiState.Loading -> MifosCircularProgress()

                is SignatureUiState.SignatureUploadedSuccessfully -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = getString(Res.string.feature_client_signature_uploaded_successfully),
                            duration = SnackbarDuration.Short
                        )
                    }
                    onBackPressed()
                }
                is SignatureUiState.Initial -> drawingContent()
            }
        }
    }
}

private data class BottomNavigationItem(
    val label: String = "",
    val icon: ImageVector = MifosIcons.Close,
    val route: String = "",
) {

    @Composable
    fun bottomNavigationItems(): List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = stringResource(Res.string.feature_client_signature_reset),
                icon = MifosIcons.Close,
            ),
            BottomNavigationItem(
                label = stringResource(Res.string.feature_client_signature_gallery),
                icon = MifosIcons.Gallery,
            ),
        )
    }
}

private class SignatureScreenUiStateProvider : PreviewParameterProvider<SignatureUiState> {

    override val values: Sequence<SignatureUiState>
        get() = sequenceOf(
            SignatureUiState.Initial,
            SignatureUiState.Error(message = Res.string.feature_client_failed_to_add_signature),
            SignatureUiState.Loading,
            SignatureUiState.SignatureUploadedSuccessfully,
        )
}

@DevicePreview
@Composable
private fun SignatureScreenPreview(
    @PreviewParameter(SignatureScreenUiStateProvider::class) state: SignatureUiState,
) {
    SignatureScreen(
        state = state,
        onBackPressed = {},
        onUploadFromCanvas = {},
        onUploadFromGallery = {},
        snackbarHostState = remember { SnackbarHostState() },
        drawColor = Color.Black,
        drawBrush = 5f,
        onResetDrawing = {},
        drawingContent = {}
    )
}

@DevicePreview
@Composable
private fun SignatureScreenInitialPreview() {
    SignatureScreen(
        state = SignatureUiState.Initial,
        onBackPressed = {},
        onUploadFromCanvas = {},
        onUploadFromGallery = {},
        snackbarHostState = remember { SnackbarHostState() },
        drawColor = Color.Black,
        drawBrush = 5f,
        onResetDrawing = {},
        drawingContent = { Text("Drawing Content Area") }
    )
}
