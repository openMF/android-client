package com.mifos.core.datastore

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.mifos.core.common.BuildConfig
import com.mifos.core.common.utils.asServerConfig
import com.mifos.core.model.ServerConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mifos.core.sharedpreference.BasePreferenceManager
import org.mifos.core.sharedpreference.Key
import javax.inject.Inject

class ServerConfigPrefManager @Inject constructor(
    @ApplicationContext
    private val context: Context
) : BasePreferenceManager() {

    override val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    val getServerConfig: ServerConfig =
        preference.getString(serverConfigKey.value, null)?.let {
            gson.fromJson(it, ServerConfig::class.java)
        } ?: BuildConfig.DEMO_SERVER_CONFIG.asServerConfig()

    fun updateServerConfig(config: ServerConfig?) {
        this.put(serverConfigKey, config)
    }

    companion object {
        val serverConfigKey = Key.Custom("SERVER_CONFIG_KEY")
    }
}