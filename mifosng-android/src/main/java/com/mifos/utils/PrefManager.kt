package com.mifos.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mifos.application.App
import com.mifos.core.objects.user.User
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.sharedpreference.Key
import org.mifos.core.sharedpreference.UserPreferences

/**
 * Created by Aditya Gupta on 19/08/23.
 */

object PrefManager : UserPreferences<User>() {

    private const val USER_DETAILS = "user_details"
    private const val AUTH_USERNAME = "auth_username"
    private const val AUTH_PASSWORD = "auth_password"

    override val preference: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(App.instance?.applicationContext)

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

    var userStatus: Boolean
        get() = get(Key.Custom(Constants.SERVICE_STATUS), false)
        set(status) {
            put(Key.Custom(Constants.SERVICE_STATUS), status)
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