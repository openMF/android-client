/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects

/**
 * Created by Rajan Maurya on 23/07/16.
 */
class ErrorSyncServerMessage {
    var developerMessage: String? = null
    var httpStatusCode = 0
    var defaultUserMessage: String? = null
    var userMessageGlobalisationCode: String? = null
    var errors: List<Error>? = null

    inner class Error {
        var developerMessage: String? = null
        var defaultUserMessage: String? = null
        var userMessageGlobalisationCode: String? = null
        var parameterName: String? = null
    }
}
