package com.mifos.feature.savings.account

import androidx.lifecycle.ViewModel
import com.mifos.core.data.SavingsPayload
import com.mifos.core.data.repository.SavingsAccountRepository
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate
import com.mifos.feature.savings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
@HiltViewModel
class SavingAccountViewModel @Inject constructor(private val repository: SavingsAccountRepository) :
    ViewModel() {

    private val _savingAccountUiState = MutableStateFlow<SavingAccountUiState>(SavingAccountUiState.ShowProgress)
    val savingAccountUiState: StateFlow<SavingAccountUiState> get() = _savingAccountUiState

    var clientId = 0
    var groupId = 0
    var isGroupAccount = false

    private val _savingProductsTemplate = MutableStateFlow(SavingProductsTemplate())
    val savingProductsTemplate = _savingProductsTemplate.asStateFlow()

    fun loadLoanTemplateByProduct(productId: Int) {
        if (isGroupAccount) {
            loadGroupSavingAccountTemplateByProduct(groupId, productId)
        } else {
            loadClientSavingAccountTemplateByProduct(clientId, productId)
        }
    }

    fun loadSavingsAccountsAndTemplate() {
        _savingAccountUiState.value = SavingAccountUiState.ShowProgress
        Observable.combineLatest(
            repository.savingsAccounts(),
            repository.savingsAccountTemplate()
        ) { productSavings, template -> SavingProductsAndTemplate(productSavings, template) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingProductsAndTemplate?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)
                }

                override fun onNext(productsAndTemplate: SavingProductsAndTemplate?) {
                    if (productsAndTemplate != null) {
                        _savingAccountUiState.value =
                            SavingAccountUiState.LoadAllSavings(productsAndTemplate)
                    }
                }
            })
    }

    private fun loadClientSavingAccountTemplateByProduct(clientId: Int, productId: Int) {
        repository.getClientSavingsAccountTemplateByProduct(clientId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)
                }

                override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                    _savingProductsTemplate.value =
                        savingProductsTemplate ?: SavingProductsTemplate()
                }
            })
    }

    private fun loadGroupSavingAccountTemplateByProduct(groupId: Int, productId: Int) {
        repository.getGroupSavingsAccountTemplateByProduct(groupId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_load_savings_products_and_template)
                }

                override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                    _savingProductsTemplate.value =
                        savingProductsTemplate ?: SavingProductsTemplate()
                }
            })
    }

    fun createSavingsAccount(savingsPayload: SavingsPayload?) {
        _savingAccountUiState.value = SavingAccountUiState.ShowProgress
        repository.createSavingsAccount(savingsPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Savings?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingErrorString(e.message.toString())
                }

                override fun onNext(savings: Savings?) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully(savings)
                }
            })
    }
}