/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.document.navigation

import kotlinx.serialization.Serializable

/**
 * Route for the documents list screen. Public API preserved across Wave 9 — the
 * consumers (`cmp-navigation/AuthenticatedNavigation` and
 * `feature/client/ClientNavigation`) still spell the route + the
 * `navigateToDocumentListScreen` extension exactly as before. Only the
 * underlying screen plumbing changed (MVI + BaseViewModel<S, E, A>).
 */
@Serializable
data class DocumentListRoute(
    val entityId: Int,
    val entityType: String,
)
