package com.mifos.core.common.utils

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitCameraType
import io.github.vinceglb.filekit.dialogs.openCameraPicker

actual suspend fun takePhotoIfSupported(): PlatformFile? {
    return FileKit.openCameraPicker(
        FileKitCameraType.Photo
    )
}