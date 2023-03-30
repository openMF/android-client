package com.mifos.api.datamanager

import com.mifos.objects.user.User
import com.mifos.objects.user.UserLogin
import com.mifos.utils.PrefManager
import org.apache.fineract.client.models.PostAuthenticationRequest
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.apimanager.BaseApiManager
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 19/02/17.
 */
class DataManagerAuth @Inject  constructor(private val baseApiManager: BaseApiManager) {
    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    fun login(username: String, password: String): Observable<PostAuthenticationResponse>? {
        val req = PostAuthenticationRequest()
        req.username = username
        req.password = password
        baseApiManager.createService(username,password,PrefManager.getInstanceUrl())
        return baseApiManager.getAuthApi().authenticate(req,true)
    }
}