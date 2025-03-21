package com.mifos.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview

@Composable
fun MifosUserImage(
    bitmap: ByteArray?,
    modifier: Modifier = Modifier,
    username: String? = null,
) {
    val context = LocalPlatformContext.current
    val uploadedImage by remember { mutableStateOf<ByteArray?>(null) }

    val painter = rememberAsyncImagePainter(
        model = uploadedImage,
        imageLoader = ImageLoader(context),
    )
    if (bitmap == null) {
        MifosTextUserImage(
            text = username?.firstOrNull()?.toString() ?: "M",
            modifier = modifier,
        )
    } else {
        Image(
            modifier = modifier
                .clip(CircleShape),
            painter = painter,
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
        )
    }
}

@DevicePreview
@Composable
fun MifosUserImagePreview(
    modifier: Modifier = Modifier,
) {
    MifosTheme {
        MifosUserImage(
            bitmap = null,
            modifier = modifier,
            username = "John Doe",
        )
    }
}
