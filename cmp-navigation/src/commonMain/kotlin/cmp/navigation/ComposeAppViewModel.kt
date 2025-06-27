/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package cmp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ComposeAppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val userDataFlow = userPreferencesRepository.userData
    private val appThemeFlow = userPreferencesRepository.appTheme

    val uiState: StateFlow<MainUiState> =
        combine(userDataFlow, appThemeFlow) { userData, appTheme ->
            MainUiState.Success(
                isAuthenticated = userData.isAuthenticated,
                appTheme = appTheme,
            )
        }.stateIn(
            scope = viewModelScope,
            initialValue = MainUiState.Loading,
            started = SharingStarted.Eagerly,
        )

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.logOut()
        }
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val isAuthenticated: Boolean, val appTheme: AppTheme) : MainUiState
}
