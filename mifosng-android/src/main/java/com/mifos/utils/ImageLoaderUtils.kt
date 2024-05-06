package com.mifos.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.signature.ObjectKey
import com.mifos.core.network.MifosInterceptor
import com.mifos.mifosxdroid.R

/**
 * Created by Rajan Maurya on 05/02/17.
 */
object ImageLoaderUtils {
    private fun buildImageUrl(clientId: Int): String {
        return (PrefManager.getInstanceUrl()
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120")
    }

    private fun buildGlideUrl(clientId: Int): GlideUrl {
        return GlideUrl(
            buildImageUrl(clientId), LazyHeaders.Builder()
                .addHeader(MifosInterceptor.HEADER_TENANT, PrefManager.getTenant())
                .addHeader(MifosInterceptor.HEADER_AUTH, PrefManager.getToken())
                .addHeader("Accept", "application/octet-stream")
                .build()
        )
    }

    fun loadImage(context: Context, clientId: Int, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(buildGlideUrl(clientId))
            .placeholder(R.drawable.ic_dp_placeholder)
            .error(R.drawable.ic_dp_placeholder)
            .signature(ObjectKey(System.currentTimeMillis()))
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(result: Bitmap?) {
                    // check a valid bitmap is downloaded
                    if (result?.width == 0) return
                    // set to image view
                    imageView.setImageBitmap(result)
                }
            })
    }
}