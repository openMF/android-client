package com.mifos.core.datastore

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mifos.core.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.sharedpreference.Key
import org.mifos.core.sharedpreference.UserPreferences
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 19/08/23.
 */

class PrefManager @Inject constructor(@ApplicationContext context: Context) :
    UserPreferences<User>() {

    private val USER_DETAILS = "user_details"
    private val AUTH_USERNAME = "auth_username"
    private val AUTH_PASSWORD = "auth_password"

    override val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    override fun getUser(): User {
        return get(Key.Custom(USER_DETAILS))
    }

    override fun saveUser(user: User) {
        put(Key.Custom(USER_DETAILS), user)
    }

    // Created this to store userDetails
    fun savePostAuthenticationResponse(user: PostAuthenticationResponse) {
        put(Key.Custom(USER_DETAILS), user)
    }

    fun setPermissionDeniedStatus(permissionDeniedStatus: String, status: Boolean) {
        put(Key.Custom(permissionDeniedStatus), status)
    }

    fun getPermissionDeniedStatus(permissionDeniedStatus: String): Boolean {
        return get(Key.Custom(permissionDeniedStatus), true)
    }

    var usernamePassword: Pair<String, String>
        get() = Pair(get(Key.Custom(AUTH_USERNAME)), get(Key.Custom(AUTH_PASSWORD)))
        set(value) {
            put(Key.Custom(AUTH_USERNAME), value.first)
            put(Key.Custom(AUTH_PASSWORD), value.second)
        }
}