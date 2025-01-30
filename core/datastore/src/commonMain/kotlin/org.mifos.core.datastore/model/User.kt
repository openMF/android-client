package org.mifos.core.datastore.model

import kotlinx.serialization.Serializable
@Serializable
data class UserData(
    val userId: Long,
    val userName: String,
    val clientId: Long,
    val isAuthenticated: Boolean
) {
    companion object {
        val DEFAULT = UserData(
            userId = -1,
            userName = "",
            clientId = -1,
            isAuthenticated = false,
        )
    }
}