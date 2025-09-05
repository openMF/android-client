/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.shareAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.ui.util.BaseViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ShareAccountsViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsRepository,
) : BaseViewModel<ShareAccountsUiState, ShareAccountsEvent, ShareAccountsAction>(
    initialState = ShareAccountsUiState(),
) {
    private val route = savedStateHandle.toRoute<ShareAccountsRoute>()

    override fun handleAction(action: ShareAccountsAction) {
        when (action) {
            is ShareAccountsAction.CardClicked -> handleCardClick(action.activeIndex)
            ShareAccountsAction.ToggleFiler -> toggleFilter()
            ShareAccountsAction.ToggleSearchBar -> toggleSearchBar()
            is ShareAccountsAction.ViewAccount -> sendEvent(ShareAccountsEvent.ViewAccount(action.accountId))
            ShareAccountsAction.Refresh -> fetchAllShareAccounts()
        }
    }

    init {
        fetchAllShareAccounts()
    }

    fun fetchAllShareAccounts() {
        viewModelScope.launch {
            mutableStateFlow.update {
                it.copy(
                    isLoading = true,
                )
            }
            try {
                val result = repository.getShareAccounts(route.clientId)
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        accounts = result,
                        dialogState = null,
                    )
                }
            } catch (e: Exception) {
                mutableStateFlow.update {
                    it.copy(
                        isLoading = false,
                        dialogState = ShareAccountsUiState.DialogState.Error(
                            e.message ?: "Unknown error",
                        ),
                    )
                }
            }
        }
    }

    private fun toggleFilter() {
        mutableStateFlow.update {
            it.copy(
                isFilterActive = !state.isFilterActive,
            )
        }
    }

    private fun toggleSearchBar() {
        mutableStateFlow.update {
            it.copy(
                isSearchBarActive = !state.isSearchBarActive,
            )
        }
    }

    private fun handleCardClick(index: Int) {
        mutableStateFlow.update {
            it.copy(
                isCardActive = !state.isCardActive,
                currentlyActiveIndex = index,
            )
        }
    }
}

data class ShareAccountsUiState(
    val isLoading: Boolean = true,
    val accounts: List<ShareAccounts> = emptyList(),
    val isSearchBarActive: Boolean = false,
    val isFilterActive: Boolean = false,
    val isCardActive: Boolean = false,
    val currentlyActiveIndex: Int = -1,
    val dialogState: DialogState? = null,
) {
    sealed interface DialogState {
        data class Error(val message: String) : DialogState
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
    data object Refresh : ShareAccountsAction
}
