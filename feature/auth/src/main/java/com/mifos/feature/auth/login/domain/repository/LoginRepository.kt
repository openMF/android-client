package com.mifos.feature.auth.login.domain.repository

import org.apache.fineract.client.models.PostAuthenticationResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface LoginRepository {

    fun login(username: String, password: String): Observable<PostAuthenticationResponse>

}