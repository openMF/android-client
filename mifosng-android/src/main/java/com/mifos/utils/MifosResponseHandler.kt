package com.mifos.utils

import com.mifos.utils.PrefManager.userStatus

/**
 * Created by Rajan Maurya on 08/07/16.
 */
object MifosResponseHandler {
    val response: String
        get() = when (userStatus) {
            false -> "created successfully"
            true -> "Saved into DB Successfully"
        }
}