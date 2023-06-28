package com.mifos.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.signature.StringSignature
import com.mifos.api.MifosInterceptor
import com.mifos.mifosxdroid.R
import com.mifos.utils.PrefManager.instanceUrl
import com.mifos.utils.PrefManager.tenant
import com.mifos.utils.PrefManager.token

/**
 * Created by Rajan Maurya on 05/02/17.
 */
object ImageLoaderUtils {
    private fun buildImageUrl(clientId: Int): String {
        return (instanceUrl
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120")
    }

    private fun buildGlideUrl(clientId: Int): GlideUrl {
        return GlideUrl(
            buildImageUrl(clientId), LazyHeaders.Builder()
                .addHeader(MifosInterceptor.HEADER_TENANT, tenant)
                .addHeader(MifosInterceptor.HEADER_AUTH, token)
                .addHeader("Accept", "application/octet-stream")
                .build()
        )
    }

    fun loadImage(context: Context?, clientId: Int, imageView: ImageView) {
        Glide.with(context)
            .load(buildGlideUrl(clientId))
            .asBitmap()
            .placeholder(R.drawable.ic_dp_placeholder)
            .error(R.drawable.ic_dp_placeholder)
            .signature(StringSignature(System.currentTimeMillis().toString()))
            .into(object : BitmapImageViewTarget(imageView) {
                override fun setResource(result: Bitmap) {
                    // check a valid bitmap is downloaded
                    if (result.width == 0) return
                    // set to image view
                    imageView.setImageBitmap(result)
                }
            })
    }
}