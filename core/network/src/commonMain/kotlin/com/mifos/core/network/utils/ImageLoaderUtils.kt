/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.utils

import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.ImageResult
import com.mifos.core.common.utils.getInstanceUrl
import com.mifos.core.datastore.UserPreferencesRepository
import kotlinx.coroutines.flow.first

class ImageLoaderUtils(
    private val prefManager: UserPreferencesRepository,
//    private val imageLoader: ImageLoader,
) {

    private suspend fun buildImageUrl(clientId: Int): String {
        val serverConfig = prefManager.serverConfig.first()
        return (
            serverConfig.getInstanceUrl() +
                "clients/" +
                clientId +
                "/images?maxHeight=120&maxWidth=120"
            )
    }

    //TODO : if in future the image getting api i.e https://tt.mifos.community/fineract-provider/api/v1/clients/75/images?maxHeight=150 works then change the things accordingly
    suspend fun loadImage(clientId: Int): String {
        val imageUrl = buildImageUrl(clientId)
        return imageUrl
    }

    //TODO : for future reference currently the api is not working
//    suspend fun loadImage(clientId: Int): ImageResult {
//        val request = ImageRequest.Builder(context)
//            .data(buildImageUrl(clientId))
//            .addHeader(MifosInterceptor.HEADER_TENANT, prefManager.serverConfig.tenant)
//            .addHeader(MifosInterceptor.HEADER_AUTH, prefManager.token)
//            .addHeader("Accept", "application/octet-stream")
//            .build()
//        return imageLoader.execute(request)
//    }
}

