package com.mifos.core.data.repository

interface UserVerificationRepository {
    fun recordVerification()
    fun consumeVerification(): Boolean
}
