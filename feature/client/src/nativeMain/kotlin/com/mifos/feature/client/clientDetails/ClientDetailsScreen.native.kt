package com.mifos.feature.client.clientDetails

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher

actual class PlatformPhotoLauncher {
    actual fun launch() {
    }
}

@Composable
actual fun rememberPlatformPhotoLauncher(
    clientId: Int,
    clientDetailsViewModel: ClientDetailsViewModel,
): PlatformPhotoLauncher {
    TODO("Not yet implemented")
}