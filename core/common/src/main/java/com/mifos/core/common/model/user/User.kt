/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.model.user

class User {
    var username: String? = null

    var userId = 0

    var base64EncodedAuthenticationKey: String? = null

    var isAuthenticated = false

    var officeId = 0

    var officeName: String? = null

    var roles: List<Role> = ArrayList()

    var permissions: List<String> = ArrayList()
}