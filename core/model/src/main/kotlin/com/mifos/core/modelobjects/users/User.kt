/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.modelobjects.users

import com.mifos.core.modelobjects.clients.Role

class User {
    // {"username":"User1","userId":1,"base64EncodedAuthenticationKey":"VXNlcjE6dGVjaDRtZg\u003d
    // \u003d",
    // "authenticated":true,"officeId":1,"officeName":"Office1",
    // "roles":[{"id":1,"name":"Admin","description":"Admin"}],
    // "permissions":["ALL_FUNCTIONS"],"shouldRenewPassword":false}
    var username: String? = null
    var userId = 0
    var base64EncodedAuthenticationKey: String? = null
    var isAuthenticated = false
    var officeId = 0
    var officeName: String? = null
    var roles: List<Role> = ArrayList()
    var permissions: List<String> = ArrayList()
    override fun toString(): String {
        return "User{" +
            "username='" + username + '\'' +
            ", userId=" + userId +
            ", base64EncodedAuthenticationKey='" + base64EncodedAuthenticationKey + '\'' +
            ", authenticated=" + isAuthenticated +
            ", officeId=" + officeId +
            ", officeName='" + officeName + '\'' +
            ", roles=" + roles +
            ", permissions=" + permissions +
            '}'
    }

    companion object {
        const val AUTHENTICATION_KEY = "authenticationKey"
    }
}
