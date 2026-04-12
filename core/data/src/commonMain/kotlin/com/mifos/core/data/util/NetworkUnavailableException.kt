/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.util

/**
 * Exception thrown when network is not available and the operation requires network connectivity.
 */
class NetworkUnavailableException(
    message: String = "Network is unavailable. Please check your connection and try again.",
) : Throwable(message)
