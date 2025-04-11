package com.mifos.core.network.model

import kotlinx.serialization.Serializable

/**
 * PostAuthenticationRequest
 *
 * @param password
 * @param username
 */

@Serializable
data class PostAuthenticationRequest(
    val password: String,
    val username: String )

