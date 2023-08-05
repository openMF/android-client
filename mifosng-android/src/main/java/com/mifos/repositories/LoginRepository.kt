package com.mifos.repositories

import com.mifos.objects.user.User
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */

interface LoginRepository {

    fun login(username: String, password: String) : Observable<User>

}