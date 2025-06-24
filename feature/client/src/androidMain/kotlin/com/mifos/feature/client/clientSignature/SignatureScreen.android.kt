/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientSignature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidclient.feature.client.generated.resources.Res
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDrawingCanvas
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.utility.PathState
import io.github.vinceglb.filekit.PlatformFile
import org.jetbrains.compose.resources.stringResource
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    uploadSignature: (PlatformFile) -> Unit,
) {
    val view = LocalView.current
    val context = LocalContext.current

    var navigationSelectedItem by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    var image by remember { mutableStateOf<Bitmap?>(null) }

    val drawColor = Color.Black
    val drawBrush = 5f

    val paths = remember { mutableStateListOf<PathState>() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = context.contentResolver.openInputStream(uri).use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                uploadSignature(bitmap.toPlatformFile(context))
            }
        },
    )

    MifosScaffold(
        title = stringResource(Res.string.feature_client_signature_title),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = {
                capturingViewBounds?.let { bounds ->
                    image = createBitmap(bounds.width.roundToInt(), bounds.height.roundToInt()).applyCanvas {
                        translate(-bounds.left, -bounds.top)
                        view.draw(this)
                    }
                    image?.let { uploadSignature(it.toPlatformFile(context)) }
                }
            }) {
                Icon(imageVector = MifosIcons.Upload, contentDescription = null)
            }
        },
        bottomBar = {
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = { Text(item.label) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        onClick = {
                            navigationSelectedItem = index
                            when (index) {
                                0 -> paths.clear()
                                1 -> galleryLauncher.launch("image/*")
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .onGloballyPositioned { capturingViewBounds = it.boundsInRoot() },
        ) {
            when (state) {
                is SignatureUiState.Loading -> MifosCircularProgress()

                is SignatureUiState.Error -> MifosSweetError(
                    message = stringResource(state.message),
                )

                is SignatureUiState.SignatureUploadedSuccessfully -> {
                    Toast.makeText(
                        context,
                        stringResource(Res.string.feature_client_signature_uploaded_successfully),
                        Toast.LENGTH_SHORT,
                    ).show()
                    onBackPressed()
                }

                is SignatureUiState.Initial -> {
                    paths.add(PathState(Path(), drawColor, drawBrush))
                    MifosDrawingCanvas(drawColor = drawColor, drawBrush = drawBrush)
                }
            }
        }
    }
}

private fun Bitmap.toPlatformFile(context: Context): PlatformFile {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, outputStream)

    val file = File(context.cacheDir, "signature.png").apply {
        writeBytes(outputStream.toByteArray())
    }

    return PlatformFile(file)
}
