package com.mifos.core.network.utils

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.MifosInterceptor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ImageLoaderUtils @Inject constructor(
    private val prefManager: PrefManager,
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context
) {

    private fun buildImageUrl(clientId: Int): String {
        return (prefManager.getInstanceUrl()
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120")
    }

    suspend fun loadImage(clientId: Int): ImageResult {
        val request = ImageRequest.Builder(context)
            .data(buildImageUrl(clientId))
            .addHeader(MifosInterceptor.HEADER_TENANT, prefManager.getTenant())
            .addHeader(MifosInterceptor.HEADER_AUTH, prefManager.getToken())
            .addHeader("Accept", "application/octet-stream")
            .build()
        return imageLoader.execute(request)
    }
}