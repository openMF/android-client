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

import android.graphics.BitmapFactory
import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.feature_client_failed_to_add_signature
import androidclient.feature.client.generated.resources.feature_client_signature_gallery
import androidclient.feature.client.generated.resources.feature_client_signature_reset
import androidclient.feature.client.generated.resources.feature_client_signature_title
import androidclient.feature.client.generated.resources.feature_client_signature_uploaded_successfully
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.Uri
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.utility.PathState
import com.mifos.core.ui.util.DevicePreview
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.jetbrains.skia.Bitmap
import org.koin.compose.viewmodel.koinViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

@Composable
internal fun SignatureScreen(
    onBackPressed: () -> Unit,
    viewmodel: SignatureViewModel = koinViewModel(),
) {
    val clientId by viewmodel.clientId.collectAsStateWithLifecycle()
    val state by viewmodel.signatureUiState.collectAsStateWithLifecycle()

    SignatureScreen(
        state = state,
        onBackPressed = onBackPressed,
        uploadSignature = {
            val byteArrayOutputStream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val file = File(context.cacheDir, "signature.png")
            file.outputStream().use { it.write(byteArrayOutputStream.toByteArray()) }

            viewmodel.createDocument(
                Constants.ENTITY_TYPE_CLIENTS,
                clientId,
                file.name,
                "Signature",
                file,
            )
        },
    )
}

@Composable
internal fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    uploadSignature: (Bitmap) -> Unit,
) {
    val view = LocalView.current

    var navigationSelectedItem by remember {
        mutableIntStateOf(0)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clientSignatureUploadSuccess = stringResource(Res.string.feature_client_signature_uploaded_successfully)

    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    var image by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val drawColor by remember { mutableStateOf(Color.Black) }
    val drawBrush by remember { mutableFloatStateOf(5f) }

    var paths by remember { mutableStateOf(mutableListOf<PathState>()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
                val bitmap = context.contentResolver.openInputStream(uri).use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                uploadSignature(bitmap)
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_client_signature_title),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = {
                val bounds = capturingViewBounds ?: return@IconButton
                image = Bitmap.createBitmap(
                    bounds.width.roundToInt(), bounds.height.roundToInt(),
                    Bitmap.Config.ARGB_8888,
                ).applyCanvas {
                    translate(-bounds.left, -bounds.top)
                    view.draw(this)
                }
                image?.let { uploadSignature(it) }
            }) {
                Icon(
                    imageVector = MifosIcons.Upload,
                    contentDescription = null,
                )
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
                                    0 -> {
                                        paths = mutableListOf()
                                    }

                                    1 -> {
                                        galleryLauncher.launch("image/*")
                                    }
                                }
                            },
                        )
                    }
            }
        },
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .onGloballyPositioned {
                    capturingViewBounds = it.boundsInRoot()
                },
        ) {
            when (state) {
                is SignatureUiState.Error -> MifosSweetError(message = stringResource(state.message)) {
                }

                is SignatureUiState.Loading -> MifosCircularProgress()

                is SignatureUiState.SignatureUploadedSuccessfully -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = clientSignatureUploadSuccess,
                            duration = SnackbarDuration.Short
                        )
                    }
                    onBackPressed()
                }

                is SignatureUiState.Initial -> {
                    paths.add(PathState(Path(), drawColor, drawBrush))

                    MifosDrawingCanvas(
                        drawColor = drawColor,
                        drawBrush = drawBrush,
                    )
                }
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
        uploadSignature = {},
    )
}
