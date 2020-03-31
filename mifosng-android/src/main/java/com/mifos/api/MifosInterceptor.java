/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

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

    public MifosInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request chianrequest = chain.request();
        Builder builder = chianrequest.newBuilder()
                .header(HEADER_TENANT, PrefManager.getTenant());

        if (PrefManager.isAuthenticated())
            builder.header(HEADER_AUTH, PrefManager.getToken());

        Request request = builder.build();
        return chain.proceed(request);
    }
}
