package com.mifos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.signature.StringSignature;
import com.mifos.api.MifosInterceptor;
import com.mifos.mifosxdroid.R;

/**
 * Created by Rajan Maurya on 05/02/17.
 */
public class ImageLoaderUtils {

    public static String buildImageUrl(int clientId) {
        return PrefManager.getInstanceUrl()
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120";
    }

    public static GlideUrl buildGlideUrl(int clientId) {
        return new GlideUrl(buildImageUrl(clientId), new LazyHeaders.Builder()
                .addHeader(MifosInterceptor.HEADER_TENANT, PrefManager.getTenant())
                .addHeader(MifosInterceptor.HEADER_AUTH, PrefManager.getToken())
                .addHeader("Accept", "application/octet-stream")
                .build());
    }

    public static void loadImage(Context context, int clientId, final ImageView imageView) {
        Glide.with(context)
                .load(buildGlideUrl(clientId))
                .asBitmap()
                .placeholder(R.drawable.ic_dp_placeholder)
                .error(R.drawable.ic_dp_placeholder)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap result) {
                        // check a valid bitmap is downloaded
                        if (result == null || result.getWidth() == 0)
                            return;
                        // set to image view
                        imageView.setImageBitmap(result);
                    }
                });
    }
}
