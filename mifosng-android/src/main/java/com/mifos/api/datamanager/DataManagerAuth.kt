package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.api.model.LoginData
import com.mifos.objects.user.User
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 19/02/17.
 */
@Singleton
class DataManagerAuth @Inject constructor(private val baseApiManager: BaseApiManager) {
    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    fun login(username: String, password: String): Observable<User> {
        val loginData = LoginData(username,password)
        return baseApiManager.authApi.authenticate(loginData)
    }
}