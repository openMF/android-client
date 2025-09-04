package com.mifos.feature.client.shareAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import co.touchlab.kermit.Logger
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.data.repository.ShareAccountsRepository
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.datamanager.DataManagerShare
import com.mifos.core.ui.util.BaseViewModel
import com.mifos.feature.client.savingsAccounts.SavingsAccountsRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareAccountsViewModel(
    private val repository: ShareAccountsRepository,
    private val dataManager: DataManagerShare,
    savedStateHandle: SavedStateHandle,
    private val repo: ClientDetailsRepository,
) : BaseViewModel<ShareAccountsUiState, ShareAccountsEvent, ShareAccountsAction>(
    initialState = ShareAccountsUiState(),
) {
    private val route = savedStateHandle.toRoute<ShareAccountsRoute>()

    override fun handleAction(action: ShareAccountsAction) {
        when (action) {
            is ShareAccountsAction.CardClicked -> {
                mutableStateFlow.update {
                    it.copy(
                        isCardActive = !state.isCardActive,
                        currentlyActiveIndex = action.activeIndex,
                    )
                }
            }

            ShareAccountsAction.ToggleFiler -> {
                mutableStateFlow.update {
                    it.copy(
                        isFilterActive = !state.isFilterActive,
                    )
                }
            }

            ShareAccountsAction.ToggleSearchBar -> {
                mutableStateFlow.update {
                    it.copy(
                        isSearchBarActive = !state.isSearchBarActive,
                    )
                }
            }

            is ShareAccountsAction.ViewAccount -> {
                sendEvent(ShareAccountsEvent.ViewAccount(action.accountId))
            }

            ShareAccountsAction.CloseDialog -> {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = null,
                    )
                }
            }

            ShareAccountsAction.Refresh -> {
                fetchAllShareAccounts()
            }
        }
    }

    init {
        fetchAllShareAccounts()
    }

    fun fetchAllShareAccounts() {
        Logger.d("Pronay called fetchAllShareAccounts ")
        viewModelScope.launch {
            Logger.d { "   pronay     ${dataManager.getAllShareAccounts()}" }
            mutableStateFlow.update {
                it.copy(dialogState = ShareAccountsUiState.DialogState.Loading)
            }
            try {
                val result = repo.getShareAccounts(route.clientId)
                Logger.d { "   pronay     $result" }
                mutableStateFlow.update {
                    it.copy(
                        accounts = result,
                        dialogState = null,
                    )
                }

            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        dialogState = ShareAccountsUiState.DialogState.Error(
                            e.message ?: "Unknown error",
                        ),
                    )
                }
            }
        }
    }
}

data class ShareAccountsUiState(
    val accounts: List<ShareAccounts> = emptyList(),
    val isSearchBarActive: Boolean = false,
    val isFilterActive: Boolean = false,
    val isCardActive: Boolean = false,
    val currentlyActiveIndex: Int = -1,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
        data object Loading : DialogState
    }
}

sealed interface ShareAccountsEvent {
    data class ViewAccount(val accountId: Int) : ShareAccountsEvent
}

sealed interface ShareAccountsAction {
    data object ToggleFiler : ShareAccountsAction
    data object ToggleSearchBar : ShareAccountsAction
    data class CardClicked(val activeIndex: Int) : ShareAccountsAction
    data class ViewAccount(val accountId: Int) : ShareAccountsAction
    data object CloseDialog : ShareAccountsAction
    data object Refresh : ShareAccountsAction
}