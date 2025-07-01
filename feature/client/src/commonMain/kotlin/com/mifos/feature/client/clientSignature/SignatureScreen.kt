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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.component.DrawingState
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import com.niyajali.compose.sign.ComposeSign
import com.niyajali.compose.sign.exportSignature
import com.niyajali.compose.sign.rememberSignatureState
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
internal fun SignatureScreen(
    onBackPressed: () -> Unit,
    viewmodel: SignatureViewModel = koinViewModel(),
) {
    val clientId by viewmodel.clientId.collectAsStateWithLifecycle()
    val state by viewmodel.signatureUiState.collectAsStateWithLifecycle()

    SignatureScreen(
        state= state,
        onBackPressed=onBackPressed,
        uploadSignature = { file->


//            viewmodel.createDocument(
//                Constants.ENTITY_TYPE_CLIENTS,
//                clientId,
//                file.name,
//                "Signature",
//                file,
//            )
        }
    )
}

@Composable
fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    uploadSignature: (ImageBitmap?) -> Unit,
) {

    var navigationSelectedItem by remember { mutableIntStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }
    val signatureState = rememberSignatureState()
    var size = remember { androidx.compose.ui.geometry.Size.Zero }
    val scope = rememberCoroutineScope()
    MifosScaffold(
        title = stringResource(Res.string.feature_client_signature_title),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = {
                scope.launch {
                    val data = signatureState.exportSignature(
                        width = size.width.toInt(),
                        height = size.height.toInt(),
                    )
                    uploadSignature(data)
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
                                0 -> {signatureState.clear()}
                                1 -> {}
                            }
                        },
                    )
                }
            }
        },
    ) { paddingValues ->

        ComposeSign(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(4.dp)
                .onGloballyPositioned {
                    size = it.size.toSize()
                },
            state = signatureState,
            strokeColor = MaterialTheme.colorScheme.onSurface,
            backgroundColor = Color.Transparent,
            showGrid = false,
            onSignatureUpdate = {},
        )
    }
}

data class BottomNavigationItem(
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
