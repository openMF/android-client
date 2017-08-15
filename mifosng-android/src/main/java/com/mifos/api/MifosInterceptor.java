/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import android.content.Context;
import android.content.Intent;

import com.mifos.App;
import com.mifos.mifosxdroid.online.notification.NotificationFetchService;
import com.mifos.utils.PrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * @author fomenkoo
 */
public class MifosInterceptor implements Interceptor {

    public static final String HEADER_TENANT = "Fineract-Platform-TenantId";
    public static final String HEADER_AUTH = "Authorization";
    private Context context;

    public MifosInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        context = App.getContext();
        Request chianrequest = chain.request();
        //String notificationHeader = chain.proceed(chianrequest).header("X-Notification-Refresh");
        String notificationHeader = chain.proceed(chianrequest).header("Vary");
        if (notificationHeader.equals("Accept-Encoding")) {
        //if(notificationHeader.equals("true")) {
            context.startService(new Intent(context, NotificationFetchService.class));
        }
        Builder builder = chianrequest.newBuilder()
                .header(HEADER_TENANT, PrefManager.getTenant());

        if (PrefManager.isAuthenticated())
            builder.header(HEADER_AUTH, PrefManager.getToken());

        Request request = builder.build();
        return chain.proceed(request);
    }
}
