package com.mifos.feature.loan.loan_charge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.domain.use_cases.GetListOfLoanChargesUseCase
import com.mifos.feature.loan.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoanChargeViewModel @Inject constructor(
    private val getListOfLoanChargesUseCase: GetListOfLoanChargesUseCase
) : ViewModel() {

    private val _loanChargeUiState = MutableStateFlow<LoanChargeUiState>(LoanChargeUiState.Loading)
    val loanChargeUiState = _loanChargeUiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshLoanChargeList(loanAccountNumber: Int) {
        _isRefreshing.value = true
        loadLoanChargesList(loanAccountNumber = loanAccountNumber)
        _isRefreshing.value = false
    }


    fun loadLoanChargesList(loanAccountNumber: Int) = viewModelScope.launch(Dispatchers.IO) {
        getListOfLoanChargesUseCase(loanAccountNumber).collect { result ->
            when (result) {
                is Resource.Error -> _loanChargeUiState.value =
                    LoanChargeUiState.Error(R.string.feature_loan_failed_to_load_loan_charges)

                is Resource.Loading -> _loanChargeUiState.value = LoanChargeUiState.Loading

                is Resource.Success -> _loanChargeUiState.value =
                    LoanChargeUiState.LoanChargesList(result.data ?: emptyList())
            }
        }
    }
}