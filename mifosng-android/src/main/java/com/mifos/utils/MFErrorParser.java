/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import com.google.gson.Gson;
import com.mifos.objects.mifoserror.MifosError;

public class MFErrorParser {

    public static final String LOG_TAG = "MFErrorParser";

    private static Gson gson = new Gson();

    public static MifosError parseError(String serverResponse) {
        return gson.fromJson(serverResponse, MifosError.class);
    }
}
