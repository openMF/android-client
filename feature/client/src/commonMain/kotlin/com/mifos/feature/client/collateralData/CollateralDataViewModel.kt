package com.mifos.feature.client.collateralData


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.ui.util.BaseViewModel

import com.mifos.core.network.model.CollateralItem


import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientCollateralViewmodel (
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsRepository
): BaseViewModel<CollateralUiState,CollateralEvent,CollateralAction>(
    initialState = CollateralUiState()
){
    private val route = savedStateHandle.toRoute<ClientCollateralRoute>()
    override fun handleAction(action: CollateralAction) {
        when (action) {
            is CollateralAction.CardClicked -> handleCardClicked(action.activeIndex)
            CollateralAction.ToggleFiler -> toggleFiler()
            CollateralAction.ToggleSearchBar -> toggleSearchBar()
            is CollateralAction.ViewAccount -> sendEvent(CollateralEvent.ViewAccount(action.accountId))
            CollateralAction.Refresh -> fetchAllCollateralAccount()

        }

    }
    init{
        fetchAllCollateralAccount()
    }
    private fun fetchAllCollateralAccount(){
        viewModelScope.launch {
            mutableStateFlow.update{
                it.copy(
                    isLoading = true,
                )

            }
            try{
                val result = repository.getCollateralItems()

                when(result){
                    is DataState.Success -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                accounts = result.data,
                                dialogState = null
                            )
                        }

                    }
                    is DataState.Error -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = false,
                                dialogState = CollateralUiState.DialogState.Error(result.message)
                            )
                        }
                    }
                    is DataState.Loading -> {
                        mutableStateFlow.update {
                            it.copy(
                                isLoading = true
                            )
                        }


                    }


                }



            }catch(e : Exception){
                mutableStateFlow.update{
                    it.copy(
                        isLoading = false,
                        dialogState = CollateralUiState.DialogState.Error(e.message ?: "Unknown Error")
                    )
                }
            }
        }

    }
    private fun toggleFiler() {
        mutableStateFlow.update {
            it.copy(
                isFilterActive = !state.isFilterActive,
            )
        }


    }
    private fun toggleSearchBar() {
        mutableStateFlow.update {
            it.copy(
                isSearchBarActive = !it.isSearchBarActive,
            )
        }

    }
    private fun handleCardClicked(index : Int){
        mutableStateFlow.update {
            it.copy(
                isCardActive = !it.isCardActive,
                currentlyActiveIndex = index,
            )
        }

    }


}

data class CollateralUiState(
    val isLoading : Boolean = false,
    val isFilterActive : Boolean = false,
    val accounts : List<CollateralItem> = emptyList(),
    val isSearchBarActive :Boolean = false,
    val isCardActive : Boolean = false,
    val currentlyActiveIndex: Int = -1,
    val dialogState : DialogState? = null,){

    sealed interface DialogState{
        data class Error (val message : String) : DialogState
    }




}
sealed interface CollateralEvent{
    data class ViewAccount ( val accountsId : Int) : CollateralEvent

}
sealed interface CollateralAction{
    data object ToggleFiler : CollateralAction
    data object ToggleSearchBar : CollateralAction
    data class CardClicked (val activeIndex : Int ): CollateralAction
    data class ViewAccount (val accountId : Int) : CollateralAction
    data object Refresh : CollateralAction


}
