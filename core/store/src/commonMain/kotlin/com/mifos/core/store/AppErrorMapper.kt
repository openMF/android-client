/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.store

import template.core.base.store.error.ErrorCategory
import template.core.base.store.error.categorize

/**
 * Application-level error → user-facing message mapper. Layers branded copy and
 * domain-specific exception branches on top of [categorize].
 *
 * Add domain branches above the [categorize] fallback as needed.
 */
fun mapErrorToUserMessage(error: Throwable): String = when (categorize(error)) {
    ErrorCategory.Network -> "Can't reach the server. Check your connection and try again."
    ErrorCategory.Auth -> "Your session expired. Please sign in to Mifos Field Officer again."
    ErrorCategory.RateLimit -> "Too many requests. Please wait a moment and try again."
    ErrorCategory.Server -> "Our servers are having a moment. Please try again shortly."
    ErrorCategory.Generic -> error.message ?: "Something went wrong."
}
