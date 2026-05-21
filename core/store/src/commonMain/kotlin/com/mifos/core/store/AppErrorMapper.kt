/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package com.mifos.core.store

import template.core.base.store.error.ErrorCategory
import template.core.base.store.error.categorize

/**
 * Application-level error → user-facing message mapper.
 *
 * The library default ([template.core.base.ui.defaultErrorMessage]) routes through
 * [categorize] to return generic per-category copy. This mapper layers app-specific
 * decisions on top:
 * - branded copy ("Sign in to Mifos Field Officer again." instead of "Sign in again.")
 * - domain-specific error types (Fineract API error codes — extend as features migrate)
 * - localized strings (wire `composeResources` here once string indirection lands)
 *
 * As Phase C feature waves migrate to Store5, add their Fineract-specific exception
 * branches above the [categorize] fallback.
 */
fun mapErrorToUserMessage(error: Throwable): String = when (categorize(error)) {
    ErrorCategory.Network -> "Can't reach the server. Check your connection and try again."
    ErrorCategory.Auth -> "Your session expired. Please sign in to Mifos Field Officer again."
    ErrorCategory.RateLimit -> "Too many requests. Please wait a moment and try again."
    ErrorCategory.Server -> "Our servers are having a moment. Please try again shortly."
    ErrorCategory.Generic -> error.message ?: "Something went wrong."
    // Phase C feature-wave migrations add Fineract-specific exception branches here, e.g.
    //   error is FineractValidationException -> "Account number must be 8-12 digits."
    //   error is OfflineSubmissionRejectedException -> "This submission was rejected. Pull to refresh."
}
