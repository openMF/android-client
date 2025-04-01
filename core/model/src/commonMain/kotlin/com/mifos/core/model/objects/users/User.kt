/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.users

import com.mifos.core.model.objects.clients.Role
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String? = null,
    val password: String? = null,
    val userId: Long = 0,
    val base64EncodedAuthenticationKey: String? = null,
    val isAuthenticated: Boolean = false,
    val officeId: Long = 0,
    val officeName: String? = null,
    val roles: List<Role> = emptyList(),
    val permissions: List<String> = emptyList(),
)
