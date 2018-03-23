/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;


import com.google.gson.Gson;
import com.mifos.objects.mifoserror.MifosError;

import io.reactivex.plugins.RxJavaPlugins;
import retrofit2.HttpException;


public class MFErrorParser {

    public static final String LOG_TAG = "MFErrorParser";

    private static Gson gson = new Gson();

    public static MifosError parseError(String serverResponse) {
        return gson.fromJson(serverResponse, MifosError.class);
    }

    public static String errorMessage(Throwable throwableError) {
        String errorMessage = "";
        try {
            if (throwableError instanceof HttpException) {
                errorMessage = ((HttpException) throwableError).response().errorBody().string();
                errorMessage =  MFErrorParser.parseError(errorMessage).getErrors()
                        .get(0).getDefaultUserMessage();
            }
        } catch (Throwable throwable) {
            RxJavaPlugins.getErrorHandler();
        }
        return errorMessage;
    }
}
