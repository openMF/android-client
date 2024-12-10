package com.mifos.utils

/**
 * Created by Rajan Maurya on 08/07/16.
 */
object MifosResponseHandler {
    fun getResponse(userStatus: Boolean): String {
        return when (userStatus) {
            true -> "Saved into DB Successfully"
            false -> "created successfully"
        }
    }
}