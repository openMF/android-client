package com.mifos.mifosxdroid.online.savingsaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.SavingsPayload
import com.mifos.core.objects.accounts.savings.FieldOfficerOptions
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.common.InterestType
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate
import com.mifos.mifosxdroid.R
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _savingAccountUiState = MutableLiveData<SavingAccountUiState>()

    val savingAccountUiState: LiveData<SavingAccountUiState>
        get() = _savingAccountUiState

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
                        SavingAccountUiState.ShowFetchingError(R.string.failed_to_load_savings_products_and_template)
                }

                override fun onNext(productsAndTemplate: SavingProductsAndTemplate?) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowSavingsAccounts(productsAndTemplate?.getmProductSavings())
                }
            })
    }

    fun loadClientSavingAccountTemplateByProduct(clientId: Int, productId: Int) {
        _savingAccountUiState.value = SavingAccountUiState.ShowProgress
        repository.getClientSavingsAccountTemplateByProduct(clientId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.failed_to_load_savings_products_and_template)
                }

                override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                    _savingAccountUiState.value = savingProductsTemplate?.let {
                        SavingAccountUiState.ShowSavingsAccountTemplateByProduct(
                            it
                        )
                    }
                }
            })
    }

    fun loadGroupSavingAccountTemplateByProduct(groupId: Int, productId: Int) {
        _savingAccountUiState.value = SavingAccountUiState.ShowProgress
        repository.getGroupSavingsAccountTemplateByProduct(groupId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingProductsTemplate?>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _savingAccountUiState.value =
                        SavingAccountUiState.ShowFetchingError(R.string.failed_to_load_savings_products_and_template)
                }

                override fun onNext(savingProductsTemplate: SavingProductsTemplate?) {
                    _savingAccountUiState.value = savingProductsTemplate?.let {
                        SavingAccountUiState.ShowSavingsAccountTemplateByProduct(
                            it
                        )
                    }
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

    fun filterSpinnerOptions(interestTypes: List<InterestType>?): List<String> {
        val interestNameList = ArrayList<String>()
        Observable.from(interestTypes)
            .subscribe { interestType -> interestType.value?.let { interestNameList.add(it) } }
        return interestNameList
    }

    fun filterSavingProductsNames(productSavings: List<ProductSavings>?): List<String> {
        val productsNames = ArrayList<String>()
        Observable.from(productSavings)
            .subscribe { product -> product.name?.let { productsNames.add(it) } }
        return productsNames
    }

    fun filterFieldOfficerNames(fieldOfficerOptions: List<FieldOfficerOptions>?): List<String> {
        val fieldOfficerNames = ArrayList<String>()
        Observable.from(fieldOfficerOptions)
            .subscribe { fieldOfficerOptions ->
                fieldOfficerOptions.displayName?.let {
                    fieldOfficerNames.add(
                        it
                    )
                }
            }
        return fieldOfficerNames
    }

}