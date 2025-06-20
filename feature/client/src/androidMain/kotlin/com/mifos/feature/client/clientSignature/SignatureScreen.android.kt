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

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.MifosDrawingCanvas
import com.mifos.core.designsystem.utility.PathState
import io.github.vinceglb.filekit.PlatformFile
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
actual fun SignatureScreen(onBackPressed: () -> Unit) {
    SignatureScreen(onBackPressed = onBackPressed)
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SignatureScreen(
    onBackPressed: () -> Unit,
    viewModel: SignatureViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.signatureUiState.collectAsStateWithLifecycle()
    val clientId by viewModel.clientId.collectAsStateWithLifecycle()

    val drawColor by remember { mutableStateOf(Color.Black) }
    val drawBrush by remember { mutableFloatStateOf(5f) }
    var image by remember { mutableStateOf<Bitmap?>(null) }
    var capturingViewBounds by remember { mutableStateOf<Rect?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var paths by remember { mutableStateOf(mutableListOf<PathState>()) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream).asImageBitmap().asAndroidBitmap()
                }
                bitmap?.let { uploadSignature(it, context.cacheDir, clientId, viewModel) }
            }
        },
    )

    fun onUploadFromCanvas() {
        val bounds = capturingViewBounds ?: return
        val bitmap = createBitmap(bounds.width(), bounds.height())
        image = bitmap
        image?.let { uploadSignature(it, context.cacheDir, clientId, viewModel) }
    }

    SignatureScreen(
        state = state,
        onBackPressed = onBackPressed,
        onUploadFromCanvas = ::onUploadFromCanvas,
        onUploadFromGallery = { galleryLauncher.launch("image/*") },
        snackbarHostState = snackbarHostState,
        drawColor = drawColor,
        drawBrush = drawBrush,
        onResetDrawing = { },
        modifier = Modifier.onGloballyPositioned {
            capturingViewBounds = Rect(
                it.boundsInRoot().left.toInt(),
                it.boundsInRoot().top.toInt(),
                it.boundsInRoot().right.toInt(),
                it.boundsInRoot().bottom.toInt(),
            )
        },
        drawingContent = {
            MifosDrawingCanvas(
                drawColor = drawColor,
                drawBrush = drawBrush,
            )
        },
    )
}

private fun uploadSignature(
    bitmap: Bitmap,
    cacheDir: File,
    clientId: Int,
    viewModel: SignatureViewModel,
) {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val file = File(cacheDir, "signature.png")
    file.outputStream().use { it.write(byteArrayOutputStream.toByteArray()) }

    val platformFile = PlatformFile(file.absolutePath)

    viewModel.createDocument(
        Constants.ENTITY_TYPE_CLIENTS,
        clientId,
        file.name,
        "Signature",
        platformFile,
    )
}
