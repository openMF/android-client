/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.data.repository.PreferenceRepository
import com.mifos.core.model.DarkThemeConfig
import com.mifos.core.model.ThemeBrand
import com.mifos.core.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mifos.library.passcode.data.PasscodeManager
import javax.inject.Inject

@HiltViewModel
class AndroidClientViewModel @Inject constructor(
    private val application: Application,
    private val preferenceRepository: PreferenceRepository,
    private val passcodeManager: PasscodeManager,
) : ViewModel() {

    val state = preferenceRepository.userData.map {
        MainState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainState.Loading,
    )

    val authenticated = preferenceRepository.isAuthenticated.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = false,
    )

    fun logOut() {
        viewModelScope.launch {
            preferenceRepository.logOut()
            passcodeManager.clearPasscode()
        }
    }

    fun updateConfig() {
        preferenceRepository.logOut()
        passcodeManager.clearPasscode()
        showRestartCountdownToast(application, 5)
    }
}

sealed interface MainState {
    data object Loading : MainState

    data class Success(val userData: UserData) : MainState {
        override val shouldDisableDynamicTheming = !userData.useDynamicColor

        override val shouldUseAndroidTheme: Boolean = when (userData.themeBrand) {
            ThemeBrand.DEFAULT -> false
            ThemeBrand.ANDROID -> true
        }

        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

    /**
     * Returns `true` if the state wasn't loaded yet and it should keep showing the splash screen.
     */
    fun shouldKeepSplashScreen() = this is Loading

    /**
     * Returns `true` if the dynamic color is disabled.
     */
    val shouldDisableDynamicTheming: Boolean get() = true

    /**
     * Returns `true` if the Android theme should be used.
     */
    val shouldUseAndroidTheme: Boolean get() = false

    /**
     * Returns `true` if dark theme should be used.
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}

private fun showRestartCountdownToast(context: Context, seconds: Int) {
    val countDownTimer = object : CountDownTimer(
        /* millisInFuture = */
        (seconds * 1000).toLong(),
        /* countDownInterval = */
        1000,
    ) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            Toast.makeText(
                context,
                "Restarting app in $secondsRemaining seconds",
                Toast.LENGTH_SHORT,
            ).show()
        }

        override fun onFinish() {
            context.restartApplication()
        }
    }
    countDownTimer.start()
}

private fun Context.restartApplication() {
    val packageManager: PackageManager = this.packageManager
    val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
    val componentName: ComponentName = intent.component!!
    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
    this.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}
