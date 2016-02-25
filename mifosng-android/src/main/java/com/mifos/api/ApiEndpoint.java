/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import retrofit.Endpoint;

/**
 * @author fomenkoo
 */
public class ApiEndpoint implements Endpoint {

    public static final String API_ENDPOINT = "demo.openmf.org";
    public static final String API_PATH = "/fineract-provider/api/v1";
    public static final String PROTOCOL_HTTPS = "https://";

    private String url;

    public void updateInstanceUrl(String instanceUrl) {
        this.url = instanceUrl;
    }

    @Override
    public String getUrl() {
        if (url == null)
            return PROTOCOL_HTTPS + API_ENDPOINT + API_PATH;
        return url;
    }

    @Override
    public String getName() {
        return "mifos";
    }
}
