package com.mifos.core.objects

/**
 * Created by Rajan Maurya on 23/07/16.
 */
class ErrorSyncServerMessage {
    var developerMessage: String? = null
    var httpStatusCode = 0
    var defaultUserMessage: String? = null
    var userMessageGlobalisationCode: String? = null
    var errors: List<com.mifos.core.objects.ErrorSyncServerMessage.Error>? = null

    inner class Error {
        var developerMessage: String? = null
        var defaultUserMessage: String? = null
        var userMessageGlobalisationCode: String? = null
        var parameterName: String? = null
    }
}