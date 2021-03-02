package com.mifos.mifosxdroid.online.savingsaccount

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.Savings
import com.mifos.objects.organisation.ProductSavings
import com.mifos.objects.templates.savings.SavingProductsTemplate

/**
 * Created by Rajan Maurya on 8/6/16.
 */
interface SavingsAccountMvpView : MvpView {
    fun showSavingsAccounts(productSavings: List<ProductSavings>?)
    fun showSavingsAccountCreatedSuccessfully(savings: Savings?)
    fun showSavingsAccountTemplateByProduct(savingProductsTemplate: SavingProductsTemplate)
    fun showFetchingError(errorMessage: Int)
    fun showFetchingError(errorMessage: String?)
}