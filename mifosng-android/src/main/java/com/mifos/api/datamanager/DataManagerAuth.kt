package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import com.mifos.objects.user.User
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Atharv Tare on 23/03/23.
 */
@Singleton
class DataManagerAuth @Inject constructor(private val baseApiManager: BaseApiManager) {
    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    fun login(username: String?, password: String?): Observable<User> {
        return baseApiManager.authApi.authenticate(username, password)
    }
}