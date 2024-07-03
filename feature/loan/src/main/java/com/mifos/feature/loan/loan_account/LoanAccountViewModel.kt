package com.mifos.feature.loan.loan_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.LoansPayload
import com.mifos.core.domain.use_cases.CreateLoanAccountUseCase
import com.mifos.core.domain.use_cases.GetAllLoanUseCase
import com.mifos.core.domain.use_cases.GetLoansAccountTemplateUseCase
import com.mifos.core.objects.templates.loans.LoanTemplate
import com.mifos.feature.loan.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanAccountViewModel @Inject constructor(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getLoansAccountTemplateUseCase: GetLoansAccountTemplateUseCase,
    private val createLoanAccountUseCase: CreateLoanAccountUseCase
) : ViewModel() {

    private val _loanAccountUiState =
        MutableStateFlow<LoanAccountUiState>(LoanAccountUiState.Loading)
    val loanAccountUiState = _loanAccountUiState.asStateFlow()

    private val _loanAccountTemplateUiState = MutableStateFlow(LoanTemplate())
    val loanAccountTemplateUiState = _loanAccountTemplateUiState.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch(Dispatchers.IO) {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _loanAccountUiState.value =
                    LoanAccountUiState.Error(R.string.feature_loan_failed_to_load_loan)

                is Resource.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is Resource.Success -> _loanAccountUiState.value =
                    LoanAccountUiState.AllLoan(result.data ?: emptyList())
            }
        }
    }

    fun loadLoanAccountTemplate(clientId: Int, productId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getLoansAccountTemplateUseCase(clientId, productId).collect { result ->
                when (result) {
                    is Resource.Error -> _loanAccountUiState.value =
                        LoanAccountUiState.Error(R.string.feature_loan_failed_to_load_template)

                    is Resource.Loading -> Unit

                    is Resource.Success -> _loanAccountTemplateUiState.value =
                        result.data ?: LoanTemplate()
                }
            }
        }

    fun createLoansAccount(loansPayload: LoansPayload) = viewModelScope.launch(Dispatchers.IO) {
        createLoanAccountUseCase(loansPayload).collect { result ->
            when (result) {
                is Resource.Error -> _loanAccountUiState.value =
                    LoanAccountUiState.Error(R.string.feature_loan_failed_to_create_loan_account)

                is Resource.Loading -> _loanAccountUiState.value = LoanAccountUiState.Loading

                is Resource.Success -> _loanAccountUiState.value =
                    LoanAccountUiState.LoanAccountCreatedSuccessfully
            }
        }
    }
}