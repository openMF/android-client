package com.mifos.mifosxdroid.online.loancharge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.client.Charges
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 10/08/23.
 */
@HiltViewModel
class LoanChargeViewModel @Inject constructor(private val repository: LoanChargeRepository) :
    ViewModel() {

    private val _loanChargeUiState = MutableLiveData<LoanChargeUiState>()

    val loanChargeUiState: LiveData<LoanChargeUiState>
        get() = _loanChargeUiState

    fun loadLoanChargesList(loanId: Int) {
        _loanChargeUiState.value = LoanChargeUiState.ShowProgressbar
        repository.getListOfLoanCharges(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Charges>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _loanChargeUiState.value =
                                errorMessage?.let { LoanChargeUiState.ShowFetchingError(it) }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargesPage: List<Charges>) {
                    _loanChargeUiState.value =
                        LoanChargeUiState.ShowLoanChargesList(chargesPage as MutableList<Charges>)
                }
            })
    }

}