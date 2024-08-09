package com.mifos.feature.savings.account

import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SavingAccountUiState {

    data object ShowProgress : SavingAccountUiState()

    data class ShowFetchingError(val message: Int) : SavingAccountUiState()

    data class LoadAllSavings(val savingsTemplate: SavingProductsAndTemplate) :
        SavingAccountUiState()

    data class ShowSavingsAccountCreatedSuccessfully(val savings: Savings?) : SavingAccountUiState()

    data class ShowFetchingErrorString(val message: String) : SavingAccountUiState()
}