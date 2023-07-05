package com.mifos.utils

import com.mifos.utils.PrefManager.userStatus

/**
 * Created by Rajan Maurya on 08/07/16.
 */
object MifosResponseHandler {
    val response: String
        get() = when (userStatus) {
            0 -> "created successfully"
            1 -> "Saved into DB Successfully"
            else -> ""
        }
}