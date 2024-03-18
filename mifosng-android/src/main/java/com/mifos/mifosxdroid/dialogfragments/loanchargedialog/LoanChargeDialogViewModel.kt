package com.mifos.mifosxdroid.dialogfragments.loanchargedialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.data.ChargesPayload
import com.mifos.core.objects.client.ChargeCreationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.ResponseBody
import retrofit2.HttpException
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class LoanChargeDialogViewModel @Inject constructor(private val repository: LoanChargeDialogRepository) :
    ViewModel() {

    private val _loanChargeDialogUiState = MutableLiveData<LoanChargeDialogUiState>()

    val loanChargeDialogUiState: LiveData<LoanChargeDialogUiState>
        get() = _loanChargeDialogUiState

    fun loanAllChargesV3(loanId: Int) {
        _loanChargeDialogUiState.value = LoanChargeDialogUiState.ShowProgressbar
        repository.getAllChargesV3(loanId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ResponseBody>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _loanChargeDialogUiState.value = errorMessage?.let {
                                LoanChargeDialogUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(response: ResponseBody) {
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.ShowAllChargesV3(response)
                }
            })
    }

    fun createLoanCharges(loanId: Int, chargesPayload: ChargesPayload?) {
        _loanChargeDialogUiState.value = LoanChargeDialogUiState.ShowProgressbar
        repository.createLoanCharges(loanId, chargesPayload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ChargeCreationResponse>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    try {
                        if (e is HttpException) {
                            val errorMessage = e.response()?.errorBody()
                                ?.string()
                            _loanChargeDialogUiState.value = errorMessage?.let {
                                LoanChargeDialogUiState.ShowError(
                                    it
                                )
                            }
                        }
                    } catch (throwable: Throwable) {
                        RxJavaPlugins.getInstance().errorHandler.handleError(e)
                    }
                }

                override fun onNext(chargeCreationResponse: ChargeCreationResponse) {
                    _loanChargeDialogUiState.value =
                        LoanChargeDialogUiState.ShowLoanChargesCreatedSuccessfully(
                            chargeCreationResponse
                        )
                }
            })
    }
}