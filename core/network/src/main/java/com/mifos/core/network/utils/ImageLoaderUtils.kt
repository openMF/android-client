/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.utils

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import com.mifos.core.datastore.PrefManager
import com.mifos.core.model.getInstanceUrl
import com.mifos.core.network.MifosInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageLoaderUtils @Inject constructor(
    private val prefManager: PrefManager,
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context,
) {

    private fun buildImageUrl(clientId: Int): String {
        return (
            prefManager.serverConfig.getInstanceUrl() +
                "clients/" +
                clientId +
                "/images?maxHeight=120&maxWidth=120"
            )
    }

    suspend fun loadImage(clientId: Int): ImageResult {
        val request = ImageRequest.Builder(context)
            .data(buildImageUrl(clientId))
            .addHeader(MifosInterceptor.HEADER_TENANT, prefManager.serverConfig.tenant)
            .addHeader(MifosInterceptor.HEADER_AUTH, prefManager.token)
            .addHeader("Accept", "application/octet-stream")
            .build()
        return imageLoader.execute(request)
    }
}
