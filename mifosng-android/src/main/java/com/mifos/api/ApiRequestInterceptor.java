/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.mifos.utils.PrefManager;

import retrofit.RequestInterceptor;

/**
 * @author fomenkoo
 */
public class ApiRequestInterceptor implements RequestInterceptor {

    public static final String HEADER_TENANT = "Fineract-Platform-TenantId";
    public static final String HEADER_AUTH = "Authorization";

    @Override
    public void intercept(RequestFacade request) {
        request.addHeader(HEADER_TENANT, PrefManager.getTenant());

        if (PrefManager.isAuthenticated())
            request.addHeader(HEADER_AUTH, PrefManager.getToken());
    }
}
