package com.mifos.mifosxdroid.online.grouploanaccount

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.GroupLoanPayload
import com.mifos.core.objects.accounts.loan.Loans
import com.mifos.core.objects.organisation.LoanProducts
import com.mifos.core.objects.templates.loans.AmortizationTypeOptions
import com.mifos.core.objects.templates.loans.FundOptions
import com.mifos.core.objects.templates.loans.GroupLoanTemplate
import com.mifos.core.objects.templates.loans.InterestCalculationPeriodType
import com.mifos.core.objects.templates.loans.InterestTypeOptions
import com.mifos.core.objects.templates.loans.LoanOfficerOptions
import com.mifos.core.objects.templates.loans.LoanPurposeOptions
import com.mifos.core.objects.templates.loans.TermFrequencyTypeOptions
import com.mifos.core.objects.templates.loans.TransactionProcessingStrategyOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class GroupLoanAccountViewModel @Inject constructor(private val repository: GroupLoanAccountRepository) :
    ViewModel() {

    private val _groupLoanAccountUiState = MutableLiveData<GroupLoanAccountUiState>()

    val groupLoanAccountUiState: LiveData<GroupLoanAccountUiState>
        get() = _groupLoanAccountUiState

    fun loadAllLoans() {
        _groupLoanAccountUiState.value = GroupLoanAccountUiState.ShowProgressbar
        repository.allLoans()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<LoanProducts>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(productLoans: List<LoanProducts>) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowAllLoans(productLoans)
                }
            })

    }

    fun loadGroupLoansAccountTemplate(groupId: Int, productId: Int) {
        _groupLoanAccountUiState.value = GroupLoanAccountUiState.ShowProgressbar
        repository.getGroupLoansAccountTemplate(groupId, productId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupLoanTemplate>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(groupLoanTemplate: GroupLoanTemplate) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowGroupLoanTemplate(groupLoanTemplate)
                }
            })
    }

    fun createGroupLoanAccount(loansPayload: GroupLoanPayload?) {
        _groupLoanAccountUiState.value = GroupLoanAccountUiState.ShowProgressbar
        repository.createGroupLoansAccount(loansPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Loans>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowFetchingError(e.message.toString())
                }

                override fun onNext(loans: Loans) {
                    _groupLoanAccountUiState.value =
                        GroupLoanAccountUiState.ShowGroupLoansAccountCreatedSuccessfully(loans)
                }
            })

    }

    fun filterAmortizations(amortizationTypeOptions: List<AmortizationTypeOptions>?): List<String> {
        val amortizationNameList = ArrayList<String>()
        Observable.from(amortizationTypeOptions)
            .subscribe { amortizationTypeOptions ->
                amortizationTypeOptions.value?.let {
                    amortizationNameList.add(
                        it
                    )
                }
            }
        return amortizationNameList
    }

    fun filterInterestCalculationPeriods(interestCalculationPeriodType: List<InterestCalculationPeriodType>?): List<String> {
        val interestCalculationPeriodNameList = ArrayList<String>()
        Observable.from(interestCalculationPeriodType)
            .subscribe { interestCalculationPeriodType ->
                interestCalculationPeriodType.value?.let {
                    interestCalculationPeriodNameList.add(
                        it
                    )
                }
            }
        return interestCalculationPeriodNameList
    }

    fun filterTransactionProcessingStrategies(transactionProcessingStrategyOptions: List<TransactionProcessingStrategyOptions>?): List<String> {
        val transactionProcessingStrategyNameList = ArrayList<String>()
        Observable.from(transactionProcessingStrategyOptions)
            .subscribe { transactionProcessingStrategyOptions ->
                transactionProcessingStrategyOptions.name?.let {
                    transactionProcessingStrategyNameList.add(
                        it
                    )
                }
            }
        return transactionProcessingStrategyNameList
    }

    fun filterLoanProducts(loanProducts: List<LoanProducts>?): List<String> {
        val loanProductsNameList = ArrayList<String>()
        Observable.from(loanProducts)
            .subscribe { loanProducts -> loanProducts.name?.let { loanProductsNameList.add(it) } }
        return loanProductsNameList
    }

    fun filterTermFrequencyTypes(termFrequencyTypeOptions: List<TermFrequencyTypeOptions>?): List<String> {
        val termFrequencyNameList = ArrayList<String>()
        Observable.from(termFrequencyTypeOptions)
            .subscribe { termFrequencyTypeOptions ->
                termFrequencyTypeOptions.value?.let {
                    termFrequencyNameList.add(
                        it
                    )
                }
            }
        return termFrequencyNameList
    }

    fun filterLoanPurposeTypes(loanPurposeOptions: List<LoanPurposeOptions>?): List<String> {
        val loanPurposeNameList = ArrayList<String>()
        Observable.from(loanPurposeOptions)
            .subscribe { loanPurposeOptions ->
                loanPurposeOptions.name?.let {
                    loanPurposeNameList.add(
                        it
                    )
                }
            }
        return loanPurposeNameList
    }

    fun filterInterestTypeOptions(interestTypeOptions: List<InterestTypeOptions>?): List<String> {
        val interestTypeNameList = ArrayList<String>()
        Observable.from(interestTypeOptions)
            .subscribe { interestTypeOptions ->
                interestTypeOptions.value?.let {
                    interestTypeNameList.add(
                        it
                    )
                }
            }
        return interestTypeNameList
    }

    fun filterLoanOfficers(loanOfficerOptions: List<LoanOfficerOptions>?): List<String> {
        val loanOfficerNameList = ArrayList<String>()
        Observable.from(loanOfficerOptions)
            .subscribe { loanOfficerOptions ->
                loanOfficerOptions.displayName?.let {
                    loanOfficerNameList.add(
                        it
                    )
                }
            }
        return loanOfficerNameList
    }

    fun filterFunds(fundOptions: List<FundOptions>?): List<String> {
        val fundNameList = ArrayList<String>()
        Observable.from(fundOptions)
            .subscribe { fundOptions -> fundOptions.name?.let { fundNameList.add(it) } }
        return fundNameList
    }
}