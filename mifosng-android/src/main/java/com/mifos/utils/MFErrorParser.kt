/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

import com.google.gson.Gson
import com.mifos.core.objects.mifoserror.MifosError
import retrofit2.HttpException
import rx.plugins.RxJavaPlugins

object MFErrorParser {
    const val LOG_TAG = "MFErrorParser"
    private val gson = Gson()

    @JvmStatic
    fun parseError(serverResponse: String?): MifosError {
        return gson.fromJson(serverResponse, MifosError::class.java)
    }

    @JvmStatic
    fun errorMessage(throwableError: Throwable): String? {
        var errorMessage: String? = ""
        try {
            if (throwableError is HttpException) {
                errorMessage = throwableError.response()?.errorBody()?.string()
                errorMessage = parseError(errorMessage).errors[0].defaultUserMessage
            } else {
                errorMessage = throwableError.toString()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
        }
        return errorMessage
    }
}