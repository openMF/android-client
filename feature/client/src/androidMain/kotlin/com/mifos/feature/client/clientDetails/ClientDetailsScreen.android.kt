package com.mifos.feature.client.clientDetails

import androidx.compose.runtime.Composable
import io.github.vinceglb.filekit.dialogs.compose.PhotoResultLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher

actual class PlatformPhotoLauncher internal constructor(
    private val internalLauncher: PhotoResultLauncher,
) {
    actual fun launch() {
        internalLauncher.launch()
    }
}

@Composable
actual fun rememberPlatformPhotoLauncher(
    clientId: Int,
    viewModel: ClientDetailsViewModel
): PlatformPhotoLauncher {
    val launcher = rememberCameraPickerLauncher { file ->
        file?.let { viewModel.saveClientImage(clientId, it) }
    }
    return PlatformPhotoLauncher(internalLauncher = launcher)
}
