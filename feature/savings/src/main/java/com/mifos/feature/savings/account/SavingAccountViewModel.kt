package com.mifos.feature.savings.account

import androidx.compose.runtime.key
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.SavingsPayload
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.domain.use_cases.CreateSavingsAccountUseCase
import com.mifos.core.domain.use_cases.GetClientSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.use_cases.GetGroupSavingsAccountTemplateByProductUseCase
import com.mifos.core.domain.use_cases.LoadSavingsAccountsAndTemplateUseCase
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate
import com.mifos.feature.savings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SavingAccountViewModel @Inject constructor(
    private val loadSavingsAccountsAndTemplateUseCase: LoadSavingsAccountsAndTemplateUseCase,
    private val createSavingsAccountUseCase: CreateSavingsAccountUseCase,
    private val getGroupSavingsAccountTemplateByProductUseCase: GetGroupSavingsAccountTemplateByProductUseCase,
    private val getClientSavingsAccountTemplateByProductUseCase: GetClientSavingsAccountTemplateByProductUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val groupId = savedStateHandle.getStateFlow(key = Constants.GROUP_ID, initialValue = 0)
    val clientId = savedStateHandle.getStateFlow(key = Constants.CLIENT_ID, initialValue = 0)
    val isGroupAccount = savedStateHandle.getStateFlow(key = Constants.GROUP_ACCOUNT , initialValue = false)

    private val _savingAccountUiState = MutableStateFlow<SavingAccountUiState>(SavingAccountUiState.ShowProgress)
    val savingAccountUiState: StateFlow<SavingAccountUiState> get() = _savingAccountUiState

    private val _savingProductsTemplate = MutableStateFlow(SavingProductsTemplate())
    val savingProductsTemplate = _savingProductsTemplate.asStateFlow()

    fun loadSavingsAccountsAndTemplate() =
        viewModelScope.launch(Dispatchers.IO) {
            loadSavingsAccountsAndTemplateUseCase().collect { result ->
                when (result) {
                    is Resource.Error -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)

                    is Resource.Loading -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowProgress

                    is Resource.Success -> if (result.data != null) {
                        _savingAccountUiState.value =
                            SavingAccountUiState.LoadAllSavings(result.data!!)
                    }
                }
            }
        }

    fun loadClientSavingAccountTemplateByProduct(clientId: Int, productId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getClientSavingsAccountTemplateByProductUseCase(clientId, productId).collect { result ->
                when (result) {
                    is Resource.Error -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)

                    is Resource.Loading -> Unit

                    is Resource.Success -> _savingProductsTemplate.value =
                        result.data ?: SavingProductsTemplate()
                }
            }
        }

    fun loadGroupSavingAccountTemplateByProduct(groupId: Int, productId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getGroupSavingsAccountTemplateByProductUseCase(groupId, productId).collect { result ->
                when (result) {
                    is Resource.Error -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)

                    is Resource.Loading -> Unit

                    is Resource.Success -> _savingProductsTemplate.value =
                        result.data ?: SavingProductsTemplate()
                }
            }
        }

    fun createSavingsAccount(savingsPayload: SavingsPayload?) =
        viewModelScope.launch(Dispatchers.IO) {
            createSavingsAccountUseCase(savingsPayload).collect { result ->
                when (result) {
                    is Resource.Error -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingErrorString(result.message.toString())

                    is Resource.Loading -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowProgress

                    is Resource.Success -> _savingAccountUiState.value =
                        SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully(result.data)
                }
            }
        }

}