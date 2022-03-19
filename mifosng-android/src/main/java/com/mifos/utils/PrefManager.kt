package com.mifos.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mifos.App
import com.mifos.objects.user.User
import org.mifos.core.sharedpreference.Key
import org.mifos.core.sharedpreference.UserPreferences

/**
 * @author fomenkoo
 * @see UserPreferences
 */
object  PrefManager : UserPreferences<User>() {

    private const val USER_DETAILS = "user_details"
    private const val AUTH_USERNAME = "auth_username"
    private const val AUTH_PASSWORD = "auth_password"

    override val preference: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.getInstance()
            .applicationContext)

    override fun getUser(): User {
        return get<User>(Key.Custom(USER_DETAILS))!!
    }

    override fun saveUser(user: User) {
        put(Key.Custom(USER_DETAILS), user)
    }

    fun setServiceStatus(status: Boolean) {
        put(Key.Custom(Constants.SERVICE_STATUS), status)
    }

    fun getServiceStatus(): Boolean {
        return get(Key.Custom(Constants.SERVICE_STATUS), false)
    }

    fun setPermissionDeniedStatus(permissionDeniedStatus: String, status: Boolean) {
        put(Key.Custom(permissionDeniedStatus), status)
    }

    fun getPermissionDeniedStatus(permissionDeniedStatus: String): Boolean {
        return get(Key.Custom(permissionDeniedStatus), true)
    }

    fun setUsernamePassword(username: String, password: String) {
        put(Key.Custom(AUTH_USERNAME), username)
        put(Key.Custom(AUTH_PASSWORD), password)
    }

    fun getUsernamePassword(): Pair<String, String> {
        return Pair(get(Key.Custom(AUTH_USERNAME)), get(Key.Custom(AUTH_PASSWORD)))
    }

}