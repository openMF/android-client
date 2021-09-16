package com.mifos.api.datamanager

import com.mifos.api.mappers.UserMapper
import com.mifos.objects.user.User
import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.utils.PrefManager
import org.apache.fineract.client.models.PostAuthenticationRequest
import rx.Observable

/**
 * Created by Rajan Maurya on 19/02/17.
 */
@Singleton
class DataManagerAuth @Inject constructor(
    private val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    /**
     * @param username Username
     * @param password Password
     * @return Basic OAuth
     */
    fun login(username: String?, password: String?): Observable<User> {
        sdkBaseApiManager.createService(
            username!!, password!!,
            PrefManager.getInstanceUrl(),
            PrefManager.getTenant(), false
        )
        val body = PostAuthenticationRequest()
        body.username = username
        body.password = password
        return sdkBaseApiManager.getAuthApi().authenticate(body, true)
            .map { UserMapper.mapFromEntity(it) }
    }
}