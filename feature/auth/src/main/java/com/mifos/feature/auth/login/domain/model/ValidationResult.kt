package com.mifos.feature.auth.login.domain.model

/**
 * Created by Aditya Gupta on 11/02/24.
 */

data class ValidationResult(

    val success: Boolean,

    val message: Int? = null
)