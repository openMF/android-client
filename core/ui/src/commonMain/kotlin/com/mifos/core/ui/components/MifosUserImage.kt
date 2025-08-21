/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.profile
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.ui.util.DevicePreview
import org.jetbrains.compose.resources.painterResource

@Composable
fun MifosUserImage(
    bitmap: ByteArray?,
    modifier: Modifier = Modifier,
    username: String? = null,
) {
    val context = LocalPlatformContext.current

    val painter = rememberAsyncImagePainter(
        model = bitmap,
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

@Composable
fun MifosUserImage(
    bitmap: ByteArray?,
    modifier: Modifier = Modifier,
) {
    val context = LocalPlatformContext.current

    val painter = if (bitmap != null) {
        rememberAsyncImagePainter(
            model = bitmap,
            imageLoader = ImageLoader(context),
        )
    } else {
        painterResource(Res.drawable.profile)
    }

    Image(
        modifier = modifier.clip(CircleShape),
        painter = painter,
        contentDescription = "Profile Image",
        contentScale = if (bitmap != null) ContentScale.Crop else ContentScale.Fit,
    )
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
