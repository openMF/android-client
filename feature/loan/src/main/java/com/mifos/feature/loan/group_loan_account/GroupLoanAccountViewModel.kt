package com.mifos.feature.loan.group_loan_account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.domain.use_cases.CreateGroupLoansAccountUseCase
import com.mifos.core.domain.use_cases.GetAllLoanUseCase
import com.mifos.core.domain.use_cases.GetGroupLoansAccountTemplateUseCase
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import com.mifos.feature.loan.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupLoanAccountViewModel @Inject constructor(
    private val getAllLoanUseCase: GetAllLoanUseCase,
    private val getGroupLoansAccountTemplateUseCase: GetGroupLoansAccountTemplateUseCase,
    private val createGroupLoansAccountUseCase: CreateGroupLoansAccountUseCase
) : ViewModel() {

    private val _groupLoanAccountUiState =
        MutableStateFlow<GroupLoanAccountUiState>(GroupLoanAccountUiState.Loading)
    val groupLoanAccountUiState = _groupLoanAccountUiState.asStateFlow()

    private val _loanProducts = MutableStateFlow<List<LoanProducts>>(emptyList())
    val loanProducts = _loanProducts.asStateFlow()

    fun loadAllLoans() = viewModelScope.launch(Dispatchers.IO) {
        getAllLoanUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _groupLoanAccountUiState.value =
                    GroupLoanAccountUiState.Error(R.string.feature_loan_failed_to_load_loan)

                is Resource.Loading -> _groupLoanAccountUiState.value =
                    GroupLoanAccountUiState.Loading

                is Resource.Success -> _loanProducts.value = result.data ?: emptyList()
            }
        }
    }

    fun loadGroupLoansAccountTemplate(groupId: Int, productId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            getGroupLoansAccountTemplateUseCase(groupId, productId).collect { result ->
                when (result) {
                    is Resource.Error -> _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Error(R.string.feature_loan_failed_to_load_template)

                    is Resource.Loading -> Unit

                    is Resource.Success -> _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.GroupLoanAccountTemplate(
                            result.data ?: GroupLoanTemplate()
                        )
                }

            }
        }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload) =
        viewModelScope.launch(Dispatchers.IO) {
            createGroupLoansAccountUseCase(loansPayload).collect { result ->
                when (result) {
                    is Resource.Error -> _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Error(R.string.feature_loan_failed_to_create_loan_account)

                    is Resource.Loading -> _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.Loading

                    is Resource.Success -> _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.GroupLoanAccountCreatedSuccessfully
                }
            }
        }
}