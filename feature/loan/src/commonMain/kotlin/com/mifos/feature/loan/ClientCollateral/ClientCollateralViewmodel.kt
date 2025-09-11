package com.mifos.feature.loan.ClientCollateral

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.common.utils.DataState
import com.mifos.core.data.repository.ClientDetailsRepository
import com.mifos.core.ui.util.BaseViewModel

import com.mifos.core.network.model.CollateralItem
import com.mifos.feature.loan.Clientcollateral.clientCollateralRoute

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientCollateralViewmodel (
    savedStateHandle: SavedStateHandle,
    private val repository: ClientDetailsRepository
): BaseViewModel<collateralUiState,collateralEvent,collateralAction>(
    initialState = collateralUiState()
){
    private val route = savedStateHandle.toRoute<clientCollateralRoute>()
    override fun handleAction(action: collateralAction) {
        when (action) {
            is collateralAction.cardClicked -> handleCardClicked(action.activeIndex)
            collateralAction.toggleFiler -> toggleFiler()
            collateralAction.toggleSearchBar -> toggleSearchBar()
            is collateralAction.viewAccount -> sendEvent(collateralEvent.viewAccount(action.accountId))
            collateralAction.refresh -> fetchAllCollateralAccount()

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
                val result = repository.getCollateralItems(route.clientId)

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
                                dialogState = collateralUiState.DialogState.Error(result.message)
                            )
                        }
                    }
                    is DataState.Loading -> {

                    }


                }



            }catch(e : Exception){
                mutableStateFlow.update{
                    it.copy(
                        isLoading = false,
                        dialogState = collateralUiState.DialogState.Error(e.message ?: "Unknown Error")
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
                isSearchBarActive = !state.isSearchBarActive,
            )
        }

    }
    private fun handleCardClicked(index : Int){
        mutableStateFlow.update {
            it.copy(
                isCardActive = !state.isCardActive,
                currentlyActiveIndex = index,
            )
        }

    }


}

    data class collateralUiState(
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
sealed interface collateralEvent{
    data class viewAccount ( val accountsId : Int) : collateralEvent

}
sealed interface collateralAction{
    data object toggleFiler : collateralAction
    data object toggleSearchBar : collateralAction
    data class cardClicked (val activeIndex : Int ): collateralAction
    data class viewAccount (val accountId : Int) : collateralAction
    data object refresh : collateralAction


}

