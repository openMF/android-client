package cmp.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.datastore.model.AppTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ComposeAppViewModel(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = userPreferencesRepository.appTheme
        .map { appTheme -> MainUiState.Success(appTheme) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainUiState.Loading,
        )
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val appTheme: AppTheme) : MainUiState
}
