package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerAuth
import com.mifos.objects.user.User
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */

class LoginRepositoryImp @Inject constructor(private val dataManagerAuth: DataManagerAuth) : LoginRepository{
    override fun login(username: String, password: String): Observable<User> {
        return dataManagerAuth.login(username,password)
    }
}