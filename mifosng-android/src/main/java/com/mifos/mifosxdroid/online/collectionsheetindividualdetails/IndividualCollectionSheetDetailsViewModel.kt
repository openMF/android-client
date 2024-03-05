package com.mifos.mifosxdroid.online.collectionsheetindividualdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.network.model.IndividualCollectionSheetPayload
import com.mifos.core.objects.accounts.loan.PaymentTypeOptions
import com.mifos.core.objects.collectionsheet.ClientCollectionSheet
import com.mifos.core.objects.collectionsheet.LoanAndClientName
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class IndividualCollectionSheetDetailsViewModel @Inject constructor(private val repository: IndividualCollectionSheetDetailsRepositoryImp) :
    ViewModel() {

    private val _individualCollectionSheetDetailsUiState =
        MutableLiveData<IndividualCollectionSheetDetailsUiState>()

    val individualCollectionSheetDetailsUiState: LiveData<IndividualCollectionSheetDetailsUiState>
        get() = _individualCollectionSheetDetailsUiState

    fun submitIndividualCollectionSheet(individualCollectionSheetPayload: IndividualCollectionSheetPayload?) {
        _individualCollectionSheetDetailsUiState.value =
            IndividualCollectionSheetDetailsUiState.ShowProgressbar
        repository
            .saveIndividualCollectionSheet(individualCollectionSheetPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse?>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _individualCollectionSheetDetailsUiState.value = errorMessage?.let {
                                IndividualCollectionSheetDetailsUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(genericResponse: GenericResponse?) {
                    _individualCollectionSheetDetailsUiState.value =
                        IndividualCollectionSheetDetailsUiState.ShowSuccess
                }
            })
    }

    fun filterPaymentTypeOptions(paymentTypeOptionsList: List<PaymentTypeOptions>?): List<String> {
        val paymentList: MutableList<String> = ArrayList()
        Observable.from(paymentTypeOptionsList)
            .subscribe { paymentTypeOption -> paymentTypeOption.name?.let { paymentList.add(it) } }
        return paymentList
    }

    fun filterLoanAndClientNames(clientCollectionSheets: List<ClientCollectionSheet>?): List<LoanAndClientName> {
        val loansAndClientNames: MutableList<LoanAndClientName> = ArrayList()
        Observable.from(clientCollectionSheets)
            .subscribe { clientCollectionSheet ->
                if (clientCollectionSheet.loans != null) {
                    for (loanCollectionSheet in clientCollectionSheet.loans!!) {
                        loansAndClientNames.add(
                            LoanAndClientName(
                                loanCollectionSheet,
                                clientCollectionSheet.clientName,
                                clientCollectionSheet.clientId
                            )
                        )
                    }
                }
            }
        return loansAndClientNames
    }
}