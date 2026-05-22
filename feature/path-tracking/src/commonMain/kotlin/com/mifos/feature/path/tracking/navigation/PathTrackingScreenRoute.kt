/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking.navigation

import kotlinx.serialization.Serializable

/**
 * Navigation route for the path-tracking screen. Type-safe `@Serializable`
 * object — consumed by `androidx.navigation` via reflection-free routing.
 */
@Serializable
data object PathTrackingScreenRoute
