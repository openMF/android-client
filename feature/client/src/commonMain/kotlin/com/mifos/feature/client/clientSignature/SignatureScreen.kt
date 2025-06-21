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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Constants
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.ui.util.DevicePreview
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider
import org.koin.compose.viewmodel.koinViewModel

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
        uploadSignature = { file ->
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
internal expect fun SignatureScreen(
    state: SignatureUiState,
    onBackPressed: () -> Unit,
    uploadSignature: (PlatformFile) -> Unit,
)

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
