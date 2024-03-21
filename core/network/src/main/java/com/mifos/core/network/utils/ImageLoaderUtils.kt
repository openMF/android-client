package com.mifos.core.network.utils

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.MifosInterceptor
import java.net.URL
import javax.inject.Inject

class ImageLoaderUtils @Inject constructor(private val prefManager: PrefManager) {

    private fun buildImageUrl(clientId: Int): String {
        return (prefManager.getInstanceUrl()
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120")
    }

    fun buildGlideUrl(clientId: Int): URL {
        return GlideUrl(
            buildImageUrl(clientId), LazyHeaders.Builder()
                .addHeader(MifosInterceptor.HEADER_TENANT, prefManager.getTenant())
                .addHeader(MifosInterceptor.HEADER_AUTH, prefManager.getToken())
                .addHeader("Accept", "application/octet-stream")
                .build()
        ).toURL()
    }
}