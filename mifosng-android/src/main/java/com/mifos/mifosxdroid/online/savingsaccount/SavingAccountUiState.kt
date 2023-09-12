package com.mifos.mifosxdroid.online.savingsaccount

import com.mifos.objects.client.Savings
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class SavingAccountUiState {

    object ShowProgress : SavingAccountUiState()

    data class ShowFetchingError(val message: Int) : SavingAccountUiState()

    data class ShowSavingsAccounts(val getProductSaving: List<ProductSavings>?) :
        SavingAccountUiState()

    data class ShowSavingsAccountTemplateByProduct(val savingProductsTemplate: SavingProductsTemplate) :
        SavingAccountUiState()

    data class ShowSavingsAccountCreatedSuccessfully(val savings: Savings?) : SavingAccountUiState()

    data class ShowFetchingErrorString(val message: String) : SavingAccountUiState()
}